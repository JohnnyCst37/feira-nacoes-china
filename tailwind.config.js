/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Cores semânticas conforme o Design System Royal Strategy / Ghost Aesthetic
        // Ex: ouro/amarelo imperial chinês, vermelho imperial, tons escuros sofisticados
        chinese: {
          red: '#C8102E',      // Vermelho Imperial
          gold: '#FFD700',     // Amarelo/Ouro Imperial
          golddark: '#B8860B',
          dark: '#0A0A0A',     // Fundo escuro profundo
          gray: '#1E1E1E',     // Painéis escuros
        }
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      }
    },
  },
  plugins: [],
}
