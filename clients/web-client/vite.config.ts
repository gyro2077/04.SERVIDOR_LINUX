import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { TanStackRouterVite } from "@tanstack/router-plugin/vite";
import tailwindcss from "@tailwindcss/vite";

// Proyecto Grupo 3 - Arquitectura P1
// Inicia en puerto 8081
export default defineConfig({
  plugins: [TanStackRouterVite(), react(), tailwindcss()],
  server: {
    host: "localhost",
    port: 8081,
    strictPort: false,
    cors: true,
    proxy: {
      "/soap": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/soap/, ""),
      },
    },
  },
  resolve: {
    alias: {
      "@": "/src",
    },
  },
});
