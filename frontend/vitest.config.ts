import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
        coverage: {
            provider: 'istanbul',  // you can also use 'c8' if you prefer
            reporter: ['text', 'html', 'json'], // you can use other reporters like 'lcov' or 'text-summary'
            all: true,              // Include all files, even those that were not tested
            include: ['src/**/*'],  // The files you want to include in the coverage report
            exclude: ['**/*.test.ts', '**/*.test.js'],  // Exclude test files from coverage
        },
    },
  }),
)
