"use client";

import React from "react";
import Wall from "@/components/Wall";
import Rows from "@/components/Rows";
import Floor from "@/components/Floor";
import ScoreInput from "@/components/ScoreInput";
import { MdArrowForwardIos } from "react-icons/md";

export default function Player() {
  return (
    <div className="flex flex-col items-center p-8" data-component="player">
      <div className="w-[900px] flex flex-col items-center">
        {/* Score input at the top */}
        <div className="mb-4">
          <ScoreInput />
        </div>
        
        <div className="flex flex-row justify-center items-start gap-5 w-full">
          <div className="w-[450px]" data-component="rows-container">
            <Rows />
          </div>

          <div className="w-[450px]" data-component="wall-container">
            <Wall />
          </div>
          <div data-component="floor-container">
            <Floor />
          </div>
        </div>
      </div>
    </div>
  );
}
