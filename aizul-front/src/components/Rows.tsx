import React from "react";
import ColorBlock from "./ColorBlock";
import { MdArrowForwardIos } from "react-icons/md";

type ColorName = "red" | "blue" | "yellow" | "black" | "white" | "empty";
type MaybeColor = ColorName | null;

interface RowsProps {
  colorRows: MaybeColor[][];
}

const GRID_SIZE = 5;

const Rows: React.FC<RowsProps> = ({ colorRows }) => {
  return (
    <div className="flex flex-col gap-2 p-4 bg-gray-100 rounded-lg w-[450px]">
      {Array.from({ length: GRID_SIZE }).map((_, rowIndex) => {
        const colorList = colorRows[rowIndex] || [];
        return (
          <div key={rowIndex} className="flex flex-row items-center w-full">
            {/* Blocks container */}
            <div className="flex flex-row w-[450px]">
              {Array.from({ length: GRID_SIZE }).map((_, colIndex) => {
                const color =
                  colIndex < GRID_SIZE - rowIndex - 1
                    ? null
                    : (colorList[colIndex - (GRID_SIZE - rowIndex - 1)] ??
                      null);

                return (
                  <div
                    key={colIndex}
                    className="aspect-square w-1/5 relative"
                    style={{ padding: "0.25rem" }}
                  >
                    {colIndex < GRID_SIZE - rowIndex - 1 ? (
                      <div className="w-full h-full" />
                    ) : (
                      <ColorBlock
                        colorName={color ?? "empty"}
                        initialPlaced={true}
                      />
                    )}
                  </div>
                );
              })}
            </div>
            {/* Arrow after each row */}
            <MdArrowForwardIos className="text-xl text-gray-600 ml-4" />
          </div>
        );
      })}
    </div>
  );
};

export default Rows;
