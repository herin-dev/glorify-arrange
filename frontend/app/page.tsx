"use client";

import { useCallback, useMemo, useState } from "react";
import axios from "axios";
import { useDropzone } from "react-dropzone";

type Part = "SOPRANO" | "ALTO" | "TENOR" | "BASS";

type Member = {
  name: string;
  part: Part;
  heightCm: number;
};

type Seat = {
  row: number;
  column: number;
  member: Member;
};

type RowPlacement = {
  rowNumber: number;
  staggered: boolean;
  leftPart: Part;
  rightPart: Part;
  seats: Seat[];
};

type ArrangementResult = {
  targetRows: number;
  rows: RowPlacement[];
  unplacedMembers: Member[];
};

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}

function getPartColor(part: Part) {
  switch (part) {
    case "SOPRANO":
      return "#f97316"; // orange
    case "ALTO":
      return "#22c55e"; // green
    case "TENOR":
      return "#0ea5e9"; // blue
    case "BASS":
      return "#a855f7"; // purple
    default:
      return "#9ca3af";
  }
}

export default function Home() {
  const [dragging, setDragging] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [rows, setRows] = useState(3);
  const [result, setResult] = useState<ArrangementResult | null>(null);
  const [error, setError] = useState<string | null>(null);

  const onDrop = useCallback((acceptedFiles: File[]) => {
    if (acceptedFiles.length === 0) return;
    setFile(acceptedFiles[0]);
    setError(null);
  }, []);

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    multiple: false,
    accept: {
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": [".xlsx"],
      "application/vnd.ms-excel": [".xls"],
    },
    onDragEnter: () => setDragging(true),
    onDragLeave: () => setDragging(false),
    onDropAccepted: () => setDragging(false),
  });

  const apiBase = process.env.NEXT_PUBLIC_API_URL?.replace(/\/+$/, "") || "";

  const onSubmit = async () => {
    if (!file) {
      setError("엑셀 파일을 먼저 업로드해주세요.");
      return;
    }

    const form = new FormData();
    form.append("file", file);
    form.append("rows", String(rows));

    setError(null);
    setResult(null);

    try {
      const res = await axios.post<ArrangementResult>(
        `${apiBase}/api/arrange`,
        form,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
      setResult(res.data);
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "업로드 중 오류가 발생했습니다.");
    }
  };

  const seatMap = useMemo(() => {
    if (!result) return null;
    const map: Record<number, Record<number, Seat | null>> = {};
    for (const row of result.rows) {
      map[row.rowNumber] = {};
      for (let col = 1; col <= 20; col++) {
        map[row.rowNumber][col] = null;
      }
      for (const seat of row.seats) {
        const col = Math.round(seat.column);
        if (col >= 1 && col <= 20) {
          map[row.rowNumber][col] = seat;
        }
      }
    }
    return map;
  }, [result]);

  return (
    <main className="min-h-screen bg-slate-900 text-slate-100 p-6">
      <div className="mx-auto max-w-5xl">
        <header className="mb-8">
          <h1 className="text-3xl font-bold">Glorify Arranger</h1>
          <p className="text-slate-300 mt-1">
            엑셀 업로드 후 줄 수를 조절하여 성가대 배치도를 시각화하세요.
          </p>
        </header>

        <section className="mb-8 rounded-xl border border-slate-700 bg-slate-950 p-6">
          <div
            {...getRootProps()}
            className={`flex flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed p-8 transition ${
              dragging ? "border-amber-300 bg-slate-900" : "border-slate-700 bg-slate-950"
            }`}
            style={{ cursor: "pointer" }}
          >
            <input {...getInputProps()} />
            <p className="text-sm text-slate-300">
              여기에 엑셀(.xlsx) 파일을 드래그 앤 드롭 하거나 클릭하여 선택하세요.
            </p>
            {file && <p className="text-xs text-slate-400">선택된 파일: {file.name}</p>}
          </div>

          <div className="mt-6 flex flex-col gap-4 sm:flex-row sm:items-center">
            <div className="flex-1">
              <label className="mb-1 block text-sm text-slate-300">줄 수(Row Count)</label>
              <input
                type="range"
                min={1}
                max={10}
                value={rows}
                onChange={(e) => setRows(parseInt(e.target.value, 10))}
                className="w-full"
              />
              <div className="text-xs text-slate-400">현재: {rows} 줄</div>
            </div>

            <button
              onClick={onSubmit}
              className="rounded-lg bg-emerald-500 px-6 py-3 text-sm font-semibold text-slate-950 shadow hover:bg-emerald-400"
            >
              배치 계산
            </button>
          </div>

          {error && <p className="mt-4 text-sm text-rose-400">{error}</p>}
        </section>

        {result && seatMap && (
          <section className="space-y-6">
            <div className="flex flex-wrap items-center justify-between gap-4">
              <div className="text-sm text-slate-300">
                {result.rows.length}줄 / 미배치 인원: {result.unplacedMembers.length}
              </div>
              <div className="flex gap-2 text-xs text-slate-400">
                <div className="flex items-center gap-1">
                  <span className="h-2 w-2 rounded-full bg-orange-400" /> SOPRANO
                </div>
                <div className="flex items-center gap-1">
                  <span className="h-2 w-2 rounded-full bg-emerald-500" /> ALTO
                </div>
                <div className="flex items-center gap-1">
                  <span className="h-2 w-2 rounded-full bg-sky-400" /> TENOR
                </div>
                <div className="flex items-center gap-1">
                  <span className="h-2 w-2 rounded-full bg-purple-500" /> BASS
                </div>
              </div>
            </div>

            <div className="flex flex-col gap-4">
              {result.rows.map((row) => (
                <div key={row.rowNumber} className="rounded-lg border border-slate-700 bg-slate-950 p-4">
                  <div className="mb-3 flex items-center justify-between">
                    <div className="text-sm font-semibold text-slate-100">
                      {row.rowNumber}번 줄 {row.staggered ? "(지그재그)" : ""}
                    </div>
                    <div className="text-xs text-slate-400">
                      {row.leftPart} / {row.rightPart}
                    </div>
                  </div>
                  <div className="grid grid-cols-20 gap-1">
                    {Array.from({ length: 20 }, (_, i) => {
                      const col = i + 1;
                      const seat = seatMap[row.rowNumber][col];
                      const background = seat ? getPartColor(seat.member.part) : "rgba(148,163,184,0.2)";
                      return (
                        <div
                          key={col}
                          className="h-10 rounded border border-slate-800 text-[10px] text-slate-900"
                          style={{ background }}
                          title={seat ? `${seat.member.name} (${seat.member.part})` : "빈 자리"}
                        >
                          {seat ? (
                            <div className="flex h-full flex-col items-center justify-center">
                              <span className="font-semibold text-slate-950">
                                {seat.member.name}
                              </span>
                              <span className="text-[10px] text-slate-700">{seat.member.heightCm}cm</span>
                            </div>
                          ) : null}
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          </section>
        )}
      </div>
    </main>
  );
}
