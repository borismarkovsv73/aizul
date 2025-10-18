import React, { useState } from "react";
import ColorBlock from "./ColorBlock";

type ColorName =
  | "red"
  | "blue"
  | "yellow"
  | "black"
  | "white"
  | "empty"
  | "lime";

const colors: ColorName[] = ["red", "blue", "yellow", "black", "white", "lime"];

interface CenterFactoryProps {
  isHighlighted?: boolean;
}

const CenterFactory: React.FC<CenterFactoryProps> = ({ isHighlighted = false }) => {
  const [counts, setCounts] = useState<Record<ColorName, number>>({
    red: 0,
    blue: 0,
    yellow: 0,
    black: 0,
    white: 0,
    lime: 0,
    empty: 0,
  });

  const handleCountChange = (color: ColorName, value: string) => {
    const numValue = parseInt(value) || 0;
    if (numValue >= 0) {
      if (color == "lime" && numValue > 1) return;
      setCounts((prev) => ({
        ...prev,
        [color]: numValue,
      }));
    }
  };

  return (
    <div className={`flex flex-row gap-4 p-4 bg-gray-100 shadow-lg rounded-lg transition-shadow duration-300 ${
      isHighlighted ? 'shadow-green-400 shadow-2xl ring-6 ring-green-300/50' : ''
    }`}>
      {/* Color blocks column */}
      <div className="flex flex-col gap-2">
        {colors.map((color) => (
          <div
            key={color}
            className="aspect-square w-16 relative"
            style={{ padding: "0.25rem" }}
          >
            <ColorBlock
              colorName={color}
              className=""
              disabled={true}
              initialPlaced={true}
            />
          </div>
        ))}
      </div>

      {/* Count inputs column */}
      <div className="flex flex-col gap-2">
        {colors.map((color) => (
          <div key={color} className="h-16 flex items-center">
            <input
              type="number"
              min="0"
              value={counts[color]}
              onChange={(e) => handleCountChange(color, e.target.value)}
              data-center-color={color}
              className="w-16 h-10 text-center border-2 border-gray-300 rounded-md font-bold text-lg focus:outline-none focus:border-blue-500"
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default CenterFactory;
