import React, { useState, useEffect } from "react";

type ColorName =
  | "red"
  | "blue"
  | "yellow"
  | "black"
  | "white"
  | "empty"
  | "lime";

const colorToGreekLetter: Record<ColorName, string> = {
  red: "Ω",
  blue: "Θ",
  yellow: "Δ",
  black: "Ψ",
  white: "Σ",
  lime: "I",
  empty: "",
};

interface ColorBlockProps {
  colorName: ColorName;
  initialPlaced?: boolean;
  onClick?: (isPlaced: boolean) => void;
  className?: string;
  disabled?: boolean;
}

const ColorBlock: React.FC<ColorBlockProps> = ({
  colorName = "blue",
  initialPlaced = false,
  onClick,
  className = "",
  disabled = false,
}) => {
  const [isPlaced, setIsPlaced] = useState(initialPlaced);

  // Update state when props change
  useEffect(() => {
    setIsPlaced(initialPlaced);
  }, [initialPlaced]);

  const handleClick = () => {
    if (disabled) return; // ← This is the key fix!

    const newPlacedState = !isPlaced;
    setIsPlaced(newPlacedState);
    onClick?.(newPlacedState);
  };

  const colorClasses = isPlaced
    ? `color-block-${colorName}-bold color-block-placed`
    : `color-block-${colorName}-light`;

  return (
    <div
      className={`color-block ${colorClasses} flex items-center justify-center text-xs font-bold ${className} ${
        disabled ? "cursor-not-allowed pointer-events-none" : "cursor-pointer"
      }`}
      style={{
        position: "relative",
        boxShadow: isPlaced
          ? "0.15rem 0.15rem 0.3rem rgba(0, 0, 0, 0.3)"
          : "none",
        transform: isPlaced ? "translateY(-0.15rem)" : "none",
        width: "100%",
        height: "100%",
      }}
      onClick={handleClick}
      aria-disabled={disabled}
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
          {colorToGreekLetter[colorName]}
        </span>
      )}
    </div>
  );
};

export default ColorBlock;
