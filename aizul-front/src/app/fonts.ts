import localFont from "next/font/local";

export const elektra = localFont({
  src: [
    {
      path: "../../public/fonts/elektra.ttf",
      weight: "variable",
      style: "normal",
    },
  ],
  variable: "--font-elektra",
});
