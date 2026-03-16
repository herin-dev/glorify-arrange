import "./globals.css";
import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Glorify Arranger",
  description: "Choir stage arrangement optimizer",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
