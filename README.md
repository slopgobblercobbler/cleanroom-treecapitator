# Treecapitator CleanRoom

![Fabric](https://img.shields.io/badge/Loader-Fabric-blue?style=for-the-badge) ![NeoForge](https://img.shields.io/badge/Loader-NeoForge-blue?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%20-%201.21.3+-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)
![Clean-Room](https://img.shields.io/badge/Clean--Room-100%25%20Verified-brightgreen?style=for-the-badge)

<p align="center">
  <img src="icon.png" width="128" height="128" alt="Treecapitator CleanRoom Icon"/>
</p>

> **Instantaneous recursive tree felling with anti-cabin heuristic protection.**

A genuine, from-scratch clean-room implementation compatible across **Fabric & NeoForge** for Minecraft **1.20 - 1.21.3+**.

---

## 🌟 Key Features

- **3D Recursive Connected Felling**: Instantaneously fells contiguous tree trunks using a 26-neighbor 3D Breadth-First Search (BFS) within an Euclidean radius of up to 32 blocks (max 256 logs).
- **Anti-Cabin Heuristic Defense**: Automatically validates that felled structures have a natural leaf-to-wood ratio (requiring >= 3 leaves in the upper canopy 5x5x5 bounding volume) to safeguard wooden player buildings, bridges, and cabins.
- **Sneak Inversion**: Sneaking while breaking a log seamlessly inverts functionality, felling only the targeted single log for precise woodworking.
- **Smart Leaf Canopy Decay**: Triggers cascading decay of connected leaves and aggregates all wood and sapling drops directly at the base of the trunk for instantaneous pickup.
- **Proportional Durability & Exhaustion**: Fairly deducts tool durability per log and applies vanilla-compliant food exhaustion (0.005 hunger per log).

---

## 🛡️ Clean-Room Provenance & Architecture

This mod is an independent, clean-room implementation engineered entirely from first principles:
- **Zero Third-Party Code Copying**: All algorithms, event handlers, and data structures were implemented from scratch.
- **Modern Standards**: Developed in modern Java (LTS 17 & 21) adhering strictly to standard loader lifecycles.
- **Mathematical Invariants**: Fully verified using independent 4-tier End-to-End (E2E) test suites with 100% passing assertions.

---

## 📦 Installation Guide

### Via Mod Launcher (Recommended)
1. In the **Modrinth App**, **CurseForge App**, or **Prism Launcher**, search for `Treecapitator CleanRoom`.
2. Select your loader (Fabric & NeoForge) and game version (1.20 - 1.21.3+).
3. Click **Install** to automatically download the mod and dependencies.

### Manual Installation
1. Download the latest `.jar` release matching your loader:
   - Fabric: `cleanroom_treecapitator-1.0.0-fabric-*.jar`
   - NeoForge: `cleanroom_treecapitator-1.0.0-neoforge-*.jar`
   
2. Place the downloaded `.jar` file into your `.minecraft/mods` folder.
3. Launch Minecraft and enjoy!

---

## ⚙️ Configuration

Configuration is automatically generated on first launch:
- **File**: `config/cleanroom_treecapitator.json`: configure `maxLogs` (default 256), `maxRadius` (32.0), `destroyLeaves` (true), `minLeavesRatio` (3), `sneakMode` ('INVERT').
- Edit with any text editor or via in-game configuration screens.

---

## 📄 License

This project is licensed under the terms of the **MIT License**.
See the [LICENSE](LICENSE) file for complete details.
