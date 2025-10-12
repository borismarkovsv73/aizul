import React from "react";
import ColorBlock from "./ColorBlock";

type ColorName = "red" | "blue" | "yellow" | "black" | "white" | "empty";
type MaybeColor = ColorName | null;

interface FloorProps {
  colorRow: MaybeColor[];
}

const NUMBERS = [-1, -1, -2, -2, -2, -3, -3];
const BLOCKS = 7;

const Floor: React.FC<FloorProps> = ({ colorRow }) => {
  return (
    <div className="flex flex-row items-end gap-2 p-4 bg-gray-100 rounded-lg">
      {Array.from({ length: BLOCKS }).map((_, idx) => (
        <div key={idx} className="flex flex-col items-center">
          <span className="mb-2 text-lg font-bold select-none pointer-events-none">
            {NUMBERS[idx]}
          </span>
          <div
            className="aspect-square w-12 relative"
            style={{ padding: "0.25rem" }}
          >
            <ColorBlock
              colorName={colorRow[idx] ?? "empty"}
              initialPlaced={colorRow[idx] !== null}
            />
          </div>
        </div>
      ))}
    </div>
  );
};

export default Floor;
