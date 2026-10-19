"use client";
import CenterFactory from "@/components/CenterFactory";
import Factory from "@/components/Factory";
import Player from "@/components/Player";
import { useState } from "react";
import { elektra } from "../fonts";

export default function BoardDemo() {
  const [resetKey, setResetKey] = useState(0);
  const [lastMoveResult, setLastMoveResult] = useState<any>(null);
  const [highlightedFactory, setHighlightedFactory] = useState<number | null>(
    null,
  );
  const [highlightedRow, setHighlightedRow] = useState<number | null>(null);

  const clearBoardState = () => {
    console.log("Clearing board state...");
    setResetKey((prev) => prev + 1);
    setLastMoveResult(null);
    setHighlightedFactory(null);
    setHighlightedRow(null);
    console.log("Board state cleared!");
  };

  const parseMoveForHighlights = (moveResult: any) => {
    if (!moveResult) return;

    // factoryId matches the ids sent in collectGameStateFromDOM:
    // 1-5 for factories, -1 for the center
    const factoryId = moveResult.factoryId;
    const factory =
      Number.isInteger(factoryId) &&
      (factoryId === -1 || (factoryId >= 1 && factoryId <= 5))
        ? factoryId
        : null;

    // targetRow is already 0-indexed: 0-4 for pattern lines, -1 for the floor
    const targetRow = moveResult.targetRow;
    const row =
      Number.isInteger(targetRow) && targetRow >= -1 && targetRow <= 4
        ? targetRow
        : null;

    console.log("Move highlights - Factory:", factory, "Row:", row);

    setHighlightedFactory(factory);
    setHighlightedRow(row);
  };

  const collectGameStateFromDOM = () => {
    const getColorFromElement = (element: Element): string | null => {
      const classes = element.className;
      const colorMatches = classes.match(/color-block-(\w+)-/);
      if (colorMatches && colorMatches[1] !== "empty") {
        return colorMatches[1];
      }
      return null;
    };

    const factories: any[] = [];
    let tileId = 1;

    const factoryElements = document.querySelectorAll(
      ".bg-gray-100.p-6.rounded-\\[2rem\\]",
    );
    factoryElements.forEach((factory, index) => {
      const tiles: any[] = [];
      const colorBlocks = factory.querySelectorAll(".color-block");

      colorBlocks.forEach((block) => {
        const color = getColorFromElement(block);
        if (color) {
          tiles.push({
            id: tileId++,
            color: color,
          });
        }
      });

      factories.push({
        id: index + 1,
        tiles: tiles,
        isCenter: false,
      });
    });

    const centerInputs = document.querySelectorAll("[data-center-color]");
    const centerTiles: any[] = [];

    centerInputs.forEach((input) => {
      const inputElement = input as HTMLInputElement;
      const count = parseInt(inputElement.value, 10) || 0;
      const color = inputElement.getAttribute("data-center-color");

      for (let i = 0; i < count; i++) {
        centerTiles.push({
          id: tileId++,
          color: color,
        });
      }
    });

    factories.push({
      id: -1,
      tiles: centerTiles,
      isCenter: true,
    });

    console.log(centerTiles);

    const players: any[] = [];
    const playerElements = document.querySelectorAll(
      '[data-component="player"]',
    );

    playerElements.forEach((playerEl, domIndex) => {
      // The lower, blue "Your Board" is the current player and must be
      // serialized first for the recommendation engine.
      const playerIndex = playerElements.length - 1 - domIndex;

      const rows: any[][] = [
        [null],
        [null, null],
        [null, null, null],
        [null, null, null, null],
        [null, null, null, null, null],
      ];

      const wall: any[][] = [
        [null, null, null, null, null],
        [null, null, null, null, null],
        [null, null, null, null, null],
        [null, null, null, null, null],
        [null, null, null, null, null],
      ];

      const floor: any[] = [null, null, null, null, null, null, null];

      const rowsContainer = playerEl.querySelector('[data-component="rows"]');

      if (rowsContainer) {
        const rowElements = rowsContainer.querySelectorAll("[data-row]");

        rowElements.forEach((rowEl, rowIndex) => {
          if (rowIndex < 5) {
            const colorBlocks = rowEl.querySelectorAll(".color-block");

            colorBlocks.forEach((block, blockIndex) => {
              const color = getColorFromElement(block);

              if (color && blockIndex < rows[rowIndex].length) {
                rows[rowIndex][blockIndex] = {
                  id: tileId++,
                  color: color,
                };
              }
            });
          }
        });
      }

      const wallContainer = playerEl.querySelector('[data-component="wall"]');
      if (wallContainer) {
        const wallRows = wallContainer.querySelectorAll("[data-wall-row]");

        wallRows.forEach((wallRow, rowIndex) => {
          if (rowIndex < 5) {
            const wallBlocks = wallRow.querySelectorAll(".color-block");
            wallBlocks.forEach((block, colIndex) => {
              if (colIndex < 5) {
                const color = getColorFromElement(block);

                const isPlaced = block.className.includes("color-block-placed");

                if (color && isPlaced) {
                  wall[rowIndex][colIndex] = {
                    id: 100 + rowIndex * 5 + colIndex,
                    color: color,
                  };
                }
              }
            });
          }
        });
      }

      const floorContainer = playerEl.querySelector('[data-component="floor"]');

      if (floorContainer) {
        const floorSlots = floorContainer.querySelectorAll("[data-floor-slot]");

        floorSlots.forEach((slot, index) => {
          if (index < 7) {
            const colorBlock = slot.querySelector(".color-block");
            if (colorBlock) {
              const color = getColorFromElement(colorBlock);
              if (color) {
                floor[index] = {
                  id: tileId++,
                  color: color,
                };
              }
            }
          }
        });
      }

      // Read player score from ScoreInput component
      const scoreInput = playerEl.querySelector(
        "[data-score-input]",
      ) as HTMLInputElement;
      const playerScore = scoreInput ? parseInt(scoreInput.value, 10) || 0 : 0;

      players[playerIndex] = {
        id: playerIndex + 1,
        board: {
          id: playerIndex + 1,
          rows: rows,
          wall: wall,
          floor: floor,
        },
        score: playerScore,
      };
    });

    return {
      id: 1,
      players: players.slice(0, 2),
      factories: factories,
      bag: [],
      roundNumber: 1,
    };
  };

  const exportGameState = async () => {
    try {
      console.log("Collecting game state from DOM...");
      const gameState = collectGameStateFromDOM();
      console.log("Game state collected:", gameState);

      // Check if the board is completely empty
      const hasAnyTiles =
        gameState.factories.some((factory: any) => factory.tiles.length > 0) ||
        gameState.players.some(
          (player: any) =>
            player.board.rows.some((row: any[]) =>
              row.some((tile: any) => tile !== null),
            ) ||
            player.board.wall.some((row: any[]) =>
              row.some((tile: any) => tile !== null),
            ) ||
            player.board.floor.some((tile: any) => tile !== null),
        );

      if (!hasAnyTiles) {
        alert(
          "Please set up the board with some tiles before asking for AI recommendations!",
        );
        return;
      }

      console.log("Sending to backend:", gameState);

      const backendUrls = ["http://localhost:8080/api/scenario/best_move"];

      let lastError = null;

      for (const url of backendUrls) {
        try {
          const response = await fetch(url, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
            body: JSON.stringify(gameState),
            mode: "cors", // Explicitly enable CORS
          });

          if (response.ok) {
            const moveResult = await response.json();
            console.log("Received move from backend:", moveResult);
            setLastMoveResult(moveResult);
            parseMoveForHighlights(moveResult);
            return;
          } else {
            console.error(
              `Backend error for ${url}:`,
              response.status,
              response.statusText,
            );
            lastError = `Backend error: ${response.status} ${response.statusText}`;
          }
        } catch (error) {
          console.error(`Connection error for ${url}:`, error);
          lastError = error;
        }
      }

      // If we get here, all URLs failed
      console.error("All backend URLs failed:", lastError);
      alert(
        `Error connecting to backend. Tried all URLs. Last error: ${lastError}\n\nMake sure:\n1. Backend is running on port 8080\n2. CORS is configured\n3. No firewall blocking the connection`,
      );
    } catch (error) {
      console.error("Error in exportGameState:", error);
      alert(`Unexpected error: ${error}`);
    }
  };

  return (
    <main className="flex flex-col items-center gap-8 p-8">
      <Player
        key={`opponent-${resetKey}`}
        boardRole="opponent"
        highlightedRow={null}
      />

      <section
        className="w-full max-w-[1120px]"
        aria-label="Shared tile supply"
      >
        <h2 className="mb-4 border-b border-gray-200 px-2 pb-3 text-xl font-bold text-gray-600">
          Shared supply
        </h2>

        <div className="flex flex-row items-center justify-center rounded-2xl bg-gray-100 px-12 py-8">
          <div>
            <p className="mb-4 text-xs font-bold tracking-wider text-gray-500">
              Factories
            </p>
            <div className="mr-40 grid grid-cols-3 place-items-center gap-6">
              <Factory
                key={`factory1-${resetKey}`}
                factoryId={1}
                isHighlighted={highlightedFactory === 1}
              />
              <Factory
                key={`factory2-${resetKey}`}
                factoryId={2}
                isHighlighted={highlightedFactory === 2}
              />
              <Factory
                key={`factory3-${resetKey}`}
                factoryId={3}
                isHighlighted={highlightedFactory === 3}
              />
              <Factory
                key={`factory4-${resetKey}`}
                factoryId={4}
                isHighlighted={highlightedFactory === 4}
              />
              <Factory
                key={`factory5-${resetKey}`}
                factoryId={5}
                isHighlighted={highlightedFactory === 5}
              />
            </div>
          </div>
          <div>
            <p className="mb-4 text-xs font-bold tracking-wider text-gray-500">
              Center
            </p>
            <CenterFactory
              key={`center-${resetKey}`}
              isHighlighted={highlightedFactory === -1}
            />
          </div>
        </div>
      </section>

      <Player
        key={`current-${resetKey}`}
        boardRole="current"
        highlightedRow={highlightedRow}
      />

      {/* Control buttons */}
      <div className="flex gap-4 mt-8">
        <button
          type="button"
          onClick={clearBoardState}
          className="w-40 px-6 py-3 bg-blue-accent hover:bg-blue-bold text-white font-semibold rounded-lg shadow-md transition-colors duration-200"
        >
          Clear
        </button>
        <button
          type="button"
          onClick={exportGameState}
          className="w-40 px-6 py-3 bg-blue-accent hover:bg-blue-bold text-white font-semibold rounded-lg shadow-md transition-colors duration-200"
        >
          Ask Azulie
        </button>
      </div>

      {lastMoveResult && (
        <div className="fixed bottom-4 left-4 w-150 p-6 bg-white/65 backdrop-blur-sm rounded-xl shadow-xl z-50">
          <div className="flex justify-between items-start mb-4">
            <h3
              className={`${elektra.className} font-bold text-2xl text-layout-text`}
              style={{ letterSpacing: "0.05em" }}
            >
              Azulie's Recommendation:
            </h3>
            <button
              type="button"
              onClick={() => {
                setLastMoveResult(null);
                setHighlightedFactory(null);
                setHighlightedRow(null);
              }}
              className="text-gray-400 hover:text-gray-600 text-xl font-bold leading-none"
            >
              ×
            </button>
          </div>
          <div className="space-y-3 text-base">
            <p>
              <span className="font-semibold">Best move:</span>{" "}
              {lastMoveResult.description}
            </p>
            {lastMoveResult.appliedRules && (
              <div>
                {(() => {
                  const trueRules = lastMoveResult.appliedRules.filter(
                    (rule: any) => Object.keys(rule)[0] === "true",
                  );
                  const falseRules = lastMoveResult.appliedRules.filter(
                    (rule: any) => Object.keys(rule)[0] === "false",
                  );

                  return (
                    <>
                      {trueRules.length > 0 && (
                        <div>
                          <p className="font-semibold mt-4 mb-3">
                            This move is good because:
                          </p>
                          <ul className="space-y-2 mb-4">
                            {trueRules.map((rule: any) => (
                              <li
                                key={JSON.stringify(rule)}
                                className="text-sm px-3 py-2 rounded bg-blue-light text-blue-accent font-bold"
                              >
                                {Object.values(rule)[0] as string}
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}

                      {falseRules.length > 0 && (
                        <div>
                          <p className="font-semibold mb-3">
                            But keep in mind that:
                          </p>
                          <ul className="space-y-2">
                            {falseRules.map((rule: any) => (
                              <li
                                key={JSON.stringify(rule)}
                                className="text-sm px-3 py-2 rounded bg-red-light text-red-accent font-bold"
                              >
                                {Object.values(rule)[0] as string}
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}
                    </>
                  );
                })()}
              </div>
            )}
          </div>
        </div>
      )}
    </main>
  );
}
