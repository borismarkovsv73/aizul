"use client";

import Floor from "@/components/Floor";
import Rows from "@/components/Rows";
import ScoreInput from "@/components/ScoreInput";
import Wall from "@/components/Wall";

interface PlayerProps {
  boardRole: "opponent" | "current";
  highlightedRow?: number | null;
}

export default function Player({ boardRole, highlightedRow }: PlayerProps) {
  const isCurrentBoard = boardRole === "current";

  return (
    <section
      className="flex w-full max-w-[1120px] flex-col items-center py-4"
      data-component="player"
      data-player-role={boardRole}
      aria-label={isCurrentBoard ? "Your board" : "Opponent board"}
    >
      <header className="mb-4 flex w-full items-center justify-between border-b border-gray-200 px-2 pb-3">
        <div className="flex items-center gap-3">
          <span
            className={`h-3 w-3 rounded-full ${isCurrentBoard ? "bg-blue-accent" : "bg-gray-400"}`}
          />
          <h2
            className={`text-xl font-bold ${isCurrentBoard ? "text-layout-text" : "text-gray-600"}`}
          >
            {isCurrentBoard ? "Your board" : "Opponent board"}
          </h2>
        </div>

        <ScoreInput />
      </header>

      <div className="flex w-full flex-row items-start justify-center gap-5">
        <div className="flex flex-col gap-2">
          <p className="text-xs font-bold tracking-wider text-gray-500">
            Pattern lines
          </p>
          <div className="w-[450px]">
            <Rows highlightedRow={highlightedRow} />
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <p className="text-xs font-bold tracking-wider text-gray-500">Wall</p>
          <div className="w-[450px]">
            <Wall />
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <p className="text-xs font-bold tracking-wider text-gray-500">
            Floor
          </p>
          <div>
            <Floor isHighlighted={highlightedRow === -1} />
          </div>
        </div>
      </div>
    </section>
  );
}
