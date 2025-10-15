"use client";
import React from "react";
import Player from "@/components/Player";
import Factory from "@/components/Factory";
import CenterFactory from "@/components/CenterFactory";

export default function BoardDemo() {
  return (
    <div className="flex flex-col items-center p-8 gap-8">
      <Player />
      <div className="flex flex-row items-center">
        <div className="grid grid-cols-3 gap-6 place-items-center mr-40">
          <Factory />
          <Factory />
          <Factory />
          <Factory />
          <Factory />
          <Factory />
          <Factory />
          <div />
          <div />
        </div>
        <CenterFactory />
      </div>
      <Player />
    </div>
  );
}
