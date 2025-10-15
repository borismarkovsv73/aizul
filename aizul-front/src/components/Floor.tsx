import React from "react";
import ColorBlockPicker from "./ColorBlockPicker";
import { useState } from "react";

const NUMBERS = [-1, -1, -2, -2, -2, -3, -3];
const BLOCKS = 7;
type ColorName =
  | "red"
  | "blue"
  | "yellow"
  | "black"
  | "white"
  | "lime"
  | "empty";

const Floor = () => {
  const [blockColors, setBlockColors] = useState<ColorName[]>(
    Array.from({ length: BLOCKS }, () => "empty" as ColorName),
  );

  const handleColorChange = (index: number, newColor: ColorName) => {
    const currentColor = blockColors[index];

    // If trying to place a non-empty color and it's different from current
    if (newColor == "lime" && newColor !== currentColor) {
      // Find any existing non-empty color in this row (excluding current cell)
      const existingColor = blockColors.find(
        (color, idx) => idx !== index && color == "lime",
      );

      // If there's an existing color and it's different from the new color, block it
      if (existingColor) {
        alert("You can't place more limes!");
        return false; // Return false to prevent update
      }
    }

    // Update the specific block color
    setBlockColors((prev) => {
      const newColors = [...prev];
      newColors[index] = newColor;
      return newColors;
    });

    return true; // Return true to allow update
  };

  return (
    <div className="flex flex-col justify-between p-4 bg-gray-100 rounded-lg h-[450px]">
      {Array.from({ length: BLOCKS }).map((_, idx) => (
        <div key={idx} className="flex flex-row items-center gap-4">
          <div
            className="aspect-square w-12 relative"
            style={{ padding: "0.25rem" }}
          >
            <ColorBlockPicker
              colorName={blockColors[idx]} // Use the actual color from state
              onColorChange={(newColor) => handleColorChange(idx, newColor)}
            />
          </div>
          <span className="text-lg font-bold select-none pointer-events-none">
            {NUMBERS[idx]}
          </span>
        </div>
      ))}
    </div>
  );
};

export default Floor;
