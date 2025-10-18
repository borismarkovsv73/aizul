import React, { useState, useEffect } from "react";
import { createPortal } from "react-dom";

type ColorName =
  | "red"
  | "blue"
  | "yellow"
  | "black"
  | "white"
  | "lime"
  | "empty";

// Map of color names to Greek letters
const colorToGreekLetter: Record<ColorName, string> = {
  red: "Ω",
  blue: "Θ",
  yellow: "Δ",
  black: "Ψ",
  white: "Σ",
  lime: "I",
  empty: "",
};

const allColors: ColorName[] = [
  "red",
  "blue",
  "yellow",
  "black",
  "white",
  "lime",
  "empty",
];

interface ColorBlockProps {
  colorName: ColorName;
  onColorChange?: (newColor: ColorName) => boolean | void;
  className?: string;
  excludeColors?: ColorName[]; // Colors to exclude from selection
}

const ColorBlock: React.FC<ColorBlockProps> = ({
  colorName = "empty",
  onColorChange,
  className = "",
  excludeColors = [], // Default to empty array (no exclusions)
}) => {
  const [currentColor, setCurrentColor] = useState<ColorName>(colorName);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalPosition, setModalPosition] = useState({ x: 0, y: 0 });

  // Update state when props change
  useEffect(() => {
    setCurrentColor(colorName);
  }, [colorName]);

  const handleBlockClick = (e: React.MouseEvent) => {
    setModalPosition({ x: e.clientX, y: e.clientY });
    setIsModalOpen(true);
  };

  const handleColorSelect = (newColor: ColorName) => {
    setIsModalOpen(false);

    // Call the callback first and check if it returns false
    const result = onColorChange?.(newColor);

    // Only update if the callback doesn't return false
    if (result !== false) {
      setCurrentColor(newColor);
    }
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  const isPlaced = currentColor !== "empty";
  const colorClasses = isPlaced
    ? `color-block-${currentColor}-bold color-block-placed`
    : `color-block-${currentColor}-light`;

  return (
    <>
      <div
        className={`color-block ${colorClasses} flex items-center justify-center text-xs font-bold cursor-pointer ${className}`}
        style={{
          position: "relative",
          boxShadow: isPlaced
            ? "0.15rem 0.15rem 0.3rem rgba(0, 0, 0, 0.3)"
            : "none",
          transform: isPlaced ? "translateY(-0.15rem)" : "none",
          width: "100%",
          height: "100%",
        }}
        onClick={handleBlockClick}
      >
        {isPlaced && (
          <span
            className="text-3xl font-bold select-none pointer-events-none"
            style={{
              color: "rgba(0,0,0,0.5)",
              textShadow: "0 1px 2px rgba(255,255,255,0.3)",
              fontFamily: "Georgia, 'Times New Roman', serif",
            }}
          >
            {colorToGreekLetter[currentColor]}
          </span>
        )}
      </div>

      {/* Modal */}
      {isModalOpen && createPortal(
        <div
          className="fixed inset-0 flex items-center justify-center"
          style={{ zIndex: 9999 }}
          onClick={handleModalClose}
        >
          <div
            className="bg-white rounded-lg p-6 shadow-xl"
            style={{
              position: "fixed",
              left: `${Math.min(Math.max(modalPosition.x, 150), window.innerWidth - 150)}px`,
              top: `${Math.min(Math.max(modalPosition.y, 200), window.innerHeight - 200)}px`,
              transform: "translate(-50%, -50%)",
              zIndex: 10000,
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 className="text-lg font-bold mb-4 text-gray-800">
              Select a Color
            </h3>
            <div className="grid grid-cols-3 gap-3">
              {allColors
                .filter((color) => !excludeColors.includes(color))
                .map((color) => {
                  const isCurrentColor = color === currentColor;
                  const colorClass =
                    color !== "empty"
                      ? `color-block-${color}-bold`
                      : "bg-gray-200";

                  return (
                    <button
                      key={color}
                      className={`${colorClass} w-20 h-20 rounded-lg flex flex-col items-center justify-center cursor-pointer hover:opacity-80 transition-opacity ${
                        isCurrentColor ? "ring-4 ring-blue-500" : ""
                      }`}
                      onClick={() => handleColorSelect(color)}
                      style={{
                        boxShadow: "0.15rem 0.15rem 0.3rem rgba(0, 0, 0, 0.3)",
                      }}
                    >
                      {color !== "empty" && (
                        <span
                          className="text-3xl font-bold"
                          style={{
                            color: "rgba(0,0,0,0.5)",
                            textShadow: "0 1px 2px rgba(255,255,255,0.3)",
                            fontFamily: "Georgia, 'Times New Roman', serif",
                          }}
                        >
                          {colorToGreekLetter[color]}
                        </span>
                      )}
                      <span className="text-xs mt-1 font-medium text-gray-700 capitalize">
                        {color}
                      </span>
                    </button>
                  );
                })}
            </div>
            <button
              className="mt-4 w-full bg-gray-300 hover:bg-gray-400 text-gray-800 font-semibold py-2 px-4 rounded transition-colors"
              onClick={handleModalClose}
            >
              Cancel
            </button>
          </div>
        </div>,
        document.body
      )}
    </>
  );
};

export default ColorBlock;
