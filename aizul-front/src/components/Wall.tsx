import React from "react";
import ColorBlock from "./ColorBlock";

type ColorName = "red" | "blue" | "yellow" | "black" | "white" | "empty";

const DEFAULT_PATTERN: ColorName[][] = [
  ["blue", "yellow", "red", "black", "white"],
  ["white", "blue", "yellow", "red", "black"],
  ["black", "white", "blue", "yellow", "red"],
  ["red", "black", "white", "blue", "yellow"],
  ["yellow", "red", "black", "white", "blue"],
];

const GRID_SIZE = 5;

const Wall = () => {
  return (
    <div className={`flex flex-col gap-2 p-4 bg-gray-100 rounded-lg w-[450px]`}>
      {DEFAULT_PATTERN.map((row, rowIndex) => (
        <div key={rowIndex} className="flex flex-row gap-2 w-full">
          {row.map((colorName, colIndex) => {
            return (
              <div
                key={colIndex}
                className="aspect-square w-1/5 relative"
                style={{ padding: "0.25rem" }}
              >
                <ColorBlock
                  colorName={colorName}
                  initialPlaced={false}
                  className=""
                />
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
};

export default Wall;
