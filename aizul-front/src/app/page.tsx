import Image from "next/image";
import Link from "next/link";
import { elektra } from "./layout";

export default function Home() {
  return (
    <div className="flex flex-col items-center p-8 gap-8 min-h-screen">
      {/* Header */}
      <div className="text-center mb-8">
        <div className="flex items-center justify-center gap-8 mb-4">
          <Image
            src="/logo/aizul.png"
            alt="Aizul Logo"
            width={150}
            height={150}
            className="scale-x-[-1]"
          />
          <h1 className="font-elektra text-6xl font-bold text-layout-text tracking-wider">AIZUL</h1>
          <Image
            src="/logo/aizul.png"
            alt="Aizul Logo"
            width={150}
            height={150}
          />
        </div>
        <p className="text-xl text-layout-text">Agent for Move Analysis & Recommendations</p>
      </div>

      {/* Game Overview */}
      <div className="max-w-4xl w-full bg-white rounded-2xl shadow-lg p-8 mb-8 border border-layout-bg-dark/20">
        <h2 className="text-3xl font-bold text-layout-text mb-4">About the Game</h2>
        <p className="text-lg text-layout-text leading-relaxed text-left">
          Azul is a tile-placement game where players compete to decorate the walls of the Royal Palace of Evora. 
          Our AI assistant analyzes your board state and provides optimal move recommendations using advanced 
          game theory and strategic pattern recognition.
        </p>
      </div>

      {/* Rules Grid */}
      <div className="max-w-6xl w-full grid grid-cols-1 md:grid-cols-2 gap-8">
        
        {/* How to Play */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-layout-bg-dark/20 hover:shadow-xl transition-shadow duration-300">
          <h3 className="text-2xl font-bold text-layout-text mb-4">How to Play</h3>
          <ol className="list-decimal list-inside space-y-3 text-layout-text text-left">
            <li><strong>Draft tiles:</strong> Take all tiles of the same color from a factory or the center</li>
            <li><strong>Place on rows:</strong> Add tiles to your pattern rows (1-5 tiles per row)</li>
            <li><strong>Complete rows:</strong> When a row is full, move one tile to your wall</li>
            <li><strong>Score points:</strong> Earn points for adjacent tiles on your wall</li>
            <li><strong>Repeat:</strong> Continue until someone completes a horizontal row</li>
          </ol>
        </div>

        {/* Scoring */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-layout-bg-dark/20 hover:shadow-xl transition-shadow duration-300">
          <h3 className="text-2xl font-bold text-layout-text mb-4">Scoring System</h3>
          <ul className="space-y-3 text-layout-text text-left">
            <li><strong>Single tile:</strong> 1 point</li>
            <li><strong>Adjacent tiles:</strong> 1 point per tile in connected group</li>
            <li><strong>Complete row:</strong> +2 bonus points</li>
            <li><strong>Complete column:</strong> +7 bonus points</li>
            <li><strong>Complete color:</strong> +10 bonus points</li>
            <li><strong>Floor penalties:</strong> Lose points for excess tiles</li>
          </ul>
        </div>

        {/* AI Features */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-layout-bg-dark/20 hover:shadow-xl transition-shadow duration-300">
          <h3 className="text-2xl font-bold text-layout-text mb-4">AI Analysis</h3>
          <ul className="space-y-3 text-layout-text text-left">
            <li>• Evaluates all possible moves for optimal scoring</li>
            <li>• Considers long-term strategic positioning</li>
            <li>• Analyzes opponent blocking opportunities</li>
            <li>• Calculates risk vs. reward for each move</li>
            <li>• Provides visual highlights for recommended actions</li>
          </ul>
        </div>

        {/* Game End */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-layout-bg-dark/20 hover:shadow-xl transition-shadow duration-300">
          <h3 className="text-2xl font-bold text-layout-text mb-4">Victory Conditions</h3>
          <div className="space-y-3 text-layout-text text-left">
            <p><strong>Game End:</strong> Triggered when a player completes a horizontal row on their wall.</p>
            <p><strong>Final Scoring:</strong> Bonus points awarded for completed rows, columns, and colors.</p>
            <p><strong>Victory:</strong> Player with the highest total score wins!</p>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="mt-12 text-left text-layout-text">
        <p>Azul is a trademark of Plan B Games. This is an educational implementation.</p>
      </footer>
    </div>
  );
}
