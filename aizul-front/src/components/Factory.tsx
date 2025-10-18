import React from "react";
import ColorBlockPicker from "./ColorBlockPicker";

interface FactoryProps {
  factoryId?: number;
  isHighlighted?: boolean;
}

const Factory: React.FC<FactoryProps> = ({ isHighlighted = false }) => {
  return (
    <div className={`bg-gray-100 p-6 rounded-[2rem] shadow-lg transition-all duration-300 ${
      isHighlighted ? 'shadow-green-400 shadow-2xl ring-6 ring-green-300/50' : ''
    }`}>
      <div className="grid grid-cols-2 gap-4">
        {Array.from({ length: 4 }).map((_, idx) => (
          <div key={idx} className="aspect-square w-20 relative p-1">
            <ColorBlockPicker colorName={"empty"} excludeColors={["lime"]} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Factory;
