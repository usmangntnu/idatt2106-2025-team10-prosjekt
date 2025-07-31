import { defineConfig } from 'tailwindcss'

export default defineConfig({
    theme: {
        extend: {
            keyframes: {
                fadeIn: {
                    '30': { opacity: '0', transform: 'scale(0.95)' },
                    '100%': { opacity: '1', transform: 'scale(1)' },
                },
            },
            animation: {
                fadeIn: 'fadeIn 0.4s ease-out forwards',
            },
        },
    },
    plugins: [],
})
