"use client";

import React from "react";
import Wall from "@/components/Wall";
import Rows from "@/components/Rows";
import Floor from "@/components/Floor";
import { MdArrowForwardIos } from "react-icons/md";

export default function BoardDemo() {
  return (
    <div className="flex flex-col items-center p-8">
      <div className="w-[900px] flex flex-col items-center">
        <div className="flex flex-row justify-center items-start gap-5 w-full">
          <div className="w-[450px]">
            <Rows
              colorRows={[
                ["red"],
                ["blue", "yellow"],
                [null, "black", "white"],
                ["yellow", null, "red", "blue"],
                ["white", "black", "red", "yellow", "blue"],
              ]}
            />
          </div>

          <div className="w-[450px]">
            <Wall
              placedMatrix={[
                [null, null, null, null, "black"],
                [null, null, null, "blue", "yellow"],
                [null, null, "black", "white", "red"],
                [null, "yellow", "blue", "black", "white"],
                ["red", "blue", "yellow", "black", "white"],
              ]}
            />
          </div>
        </div>
        <div className="w-full mt-8 flex justify-center">
          <Floor
            colorRow={["red", null, "blue", "yellow", null, "black", "white"]}
          />
        </div>
      </div>
    </div>
  );
}
