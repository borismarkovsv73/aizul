"use client";

import React from "react";
import Wall from "@/components/Wall";
import Rows from "@/components/Rows";
import Floor from "@/components/Floor";
import { MdArrowForwardIos } from "react-icons/md";

export default function Player() {
  return (
    <div className="flex flex-col items-center p-8">
      <div className="w-[900px] flex flex-col items-center">
        <div className="flex flex-row justify-center items-start gap-5 w-full">
          <div className="w-[450px]">
            <Rows />
          </div>

          <div className="w-[450px]">
            <Wall />
          </div>
          <Floor />
        </div>
      </div>
    </div>
  );
}
