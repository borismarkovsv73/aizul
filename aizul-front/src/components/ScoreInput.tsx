import { elektra } from "@/app/layout";
import React, { useState } from "react";

interface ScoreInputProps {
  initialScore?: number;
  onChange?: (score: number) => void;
}

const ScoreInput: React.FC<ScoreInputProps> = ({
  initialScore = 0,
  onChange
}) => {
  const [score, setScore] = useState<number>(initialScore);

  const handleScoreChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newScore = parseInt(event.target.value) || 0;
    setScore(newScore);
    onChange?.(newScore);
  };

  return (
    <div className="flex flex-row items-center gap-3 p-4 bg-gray-100 rounded-lg" data-component="score-input">
      <label className={`${elektra.className} text-xl font-bold text-gray-700 select-none`}>Score:</label>
      <input
        type="number"
        value={score}
        onChange={handleScoreChange}
        className="w-24 h-10 text-center border-2 border-gray-300 rounded-md font-bold text-lg focus:outline-none focus:border-blue-500"
        data-score-input
      />
    </div>
  );
};

export default ScoreInput;