"use client";

import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";
import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import localFont from "next/font/local";
import Image from "next/image";
import { GiGreekTemple } from "react-icons/gi";
import { BsFillGrid3X3GapFill } from "react-icons/bs";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

const elektra = localFont({
  src: [
    {
      path: "../../public/fonts/elektra.ttf",
      weight: "variable",
      style: "normal",
    },
  ],
  variable: "--font-elektra",
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const pathname = usePathname();

  const isActive = (href: string) => {
    const normalizedCurrentPath =
      pathname === "/" ? "/" : pathname.replace(/\/$/, "");
    const normalizedHref = href === "/" ? "/" : href.replace(/\/$/, "");
    return normalizedCurrentPath === normalizedHref;
  };

  return (
    <html lang="en" className="{elektra.variable}">
      <body className={`antialiased flex`}>
        <nav
          className={`${elektra.className} fixed top-0 left-0 flex flex-col items-start p-6 bg-layout-bg shadow-lg rounded-tr-xl rounded-br-xl w-64 min-h-screen h-screen text-layout-text`}
        >
          <div className="w-full flex justify-center">
            <Image
              src="/logo/aizul.png"
              alt="Aizul Logo"
              width={150}
              height={150}
            />
          </div>
          <div className="mb-8 mt-1 text-3xl font-bold flex justify-center w-full">
            Aizul
          </div>
          <Link
            href="/"
            className={`
              font-medium transition-colors duration-200 px-4 py-3 rounded-lg w-full text-left mb-2
              flex items-center
              ${isActive("/") ? "bg-layout-light-accent hover:bg-layout-light-accent-hover hover:text-layout-bg" : " hover:bg-layout-light-accent"}
            `}
          >
            <GiGreekTemple className="mr-3" /> Home
          </Link>
          <Link
            href="/board-demo"
            className={`
              font-medium transition-colors duration-200 px-4 py-3 rounded-lg w-full text-left mb-2
              flex items-center
              ${isActive("/board-demo") ? "bg-layout-light-accent hover:bg-layout-light-accent-hover hover:text-layout-bg" : " hover:bg-layout-light-accent"}
            `}
          >
            <BsFillGrid3X3GapFill className="mr-3" /> Board Demo
          </Link>
        </nav>
        <div className="flex-1 p-8 text-center text-gray-600 ml-64">
          {children || <p>Your page content goes here.</p>}
        </div>
      </body>
    </html>
  );
}
