import React, { useState } from "react";
import ColorBlockPicker from "./ColorBlockPicker";
import { MdArrowForwardIos } from "react-icons/md";

type ColorName =
  | "red"
  | "blue"
  | "yellow"
  | "black"
  | "white"
  | "lime"
  | "empty";

const GRID_SIZE = 5;

interface RowsProps {
  highlightedRow?: number | null;
}

const Rows: React.FC<RowsProps> = ({ highlightedRow }) => {
  // Track the color state of each cell [rowIndex][colIndex]
  const [gridColors, setGridColors] = useState<ColorName[][]>(
    Array.from({ length: GRID_SIZE }, () =>
      Array.from({ length: GRID_SIZE }, () => "empty" as ColorName),
    ),
  );

  const handleColorChange = (
    rowIndex: number,
    colIndex: number,
    newColor: ColorName,
  ) => {
    const currentColor = gridColors[rowIndex][colIndex];

    // If trying to place a non-empty color and it's different from current
    if (newColor !== "empty" && newColor !== currentColor) {
      // Find any existing non-empty color in this row (excluding current cell)
      const existingColor = gridColors[rowIndex].find(
        (color, idx) => color !== "empty" && idx !== colIndex,
      );

      // If there's an existing color and it's different from the new color, block it
      if (existingColor && existingColor !== newColor) {
        alert("You can't place different colors in the same row!");
        return false; // Return false to prevent update
      }
    }

    // Update the grid with the new color
    setGridColors((prev) => {
      const newGrid = prev.map((row) => [...row]);
      newGrid[rowIndex][colIndex] = newColor;
      return newGrid;
    });

    return true; // Return true to allow update
  };

  return (
    <div className="flex flex-col gap-2 p-4 bg-gray-100 rounded-lg w-[450px] h-[450px]" data-component="rows">
      {Array.from({ length: GRID_SIZE }).map((_, rowIndex) => {
        return (
          <div key={rowIndex} className={`flex flex-row items-center w-full transition-all duration-300 rounded-lg ${
            highlightedRow === rowIndex ? 'shadow-green-400 shadow-lg ring-4 ring-green-300/40' : ''
          }`} data-row={rowIndex}>
            {/* Blocks container */}
            <div className="flex flex-row w-[450px]">
              {Array.from({ length: GRID_SIZE }).map((_, colIndex) => {
                return (
                  <div
                    key={colIndex}
                    className="aspect-square w-1/5 relative"
                    style={{ padding: "0.25rem" }}
                    data-col={colIndex}
                  >
                    {colIndex < GRID_SIZE - rowIndex - 1 ? (
                      <div className="w-full h-full" />
                    ) : (
                      <ColorBlockPicker
                        colorName={gridColors[rowIndex][colIndex]}
                        onColorChange={(newColor) =>
                          handleColorChange(rowIndex, colIndex, newColor)
                        }
                        excludeColors={["lime"]}
                      />
                    )}
                  </div>
                );
              })}
            </div>
            <MdArrowForwardIos className="text-xl text-gray-600 ml-4" />
          </div>
        );
      })}
    </div>
  );
};

export default Rows;
