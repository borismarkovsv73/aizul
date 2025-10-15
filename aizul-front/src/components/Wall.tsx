import React from "react";
import ColorBlock from "./ColorBlock";

type ColorName = "red" | "blue" | "yellow" | "black" | "white" | "empty";
type MaybeColor = ColorName | null;

const DEFAULT_PATTERN: ColorName[][] = [
  ["blue", "yellow", "red", "black", "white"],
  ["white", "blue", "yellow", "red", "black"],
  ["black", "white", "blue", "yellow", "red"],
  ["red", "black", "white", "blue", "yellow"],
  ["yellow", "red", "black", "white", "blue"],
];

interface WallProps {
  placedMatrix: MaybeColor[][];
  className?: string;
}

const GRID_SIZE = 5;

const Wall: React.FC<WallProps> = ({ placedMatrix, className = "" }) => {
  return (
    <div
      className={`flex flex-col gap-2 p-4 bg-gray-100 rounded-lg w-[450px] ${className}`}
    >
      {DEFAULT_PATTERN.map((row, rowIndex) => (
        <div key={rowIndex} className="flex flex-row gap-2 w-full">
          {row.map((colorName, colIndex) => {
            const placed = placedMatrix?.[rowIndex]?.[colIndex];
            const isPlaced = placed !== null;
            return (
              <div
                key={colIndex}
                className="aspect-square w-1/5 relative"
                style={{ padding: "0.25rem" }}
              >
                <ColorBlock
                  colorName={colorName}
                  initialPlaced={isPlaced}
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
