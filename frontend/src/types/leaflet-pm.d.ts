import 'leaflet'

declare module 'leaflet' {
  interface Map {
    pm: {
      addControls: (options: any) => void
      removeControls: () => void
      enableDraw: (shape: string, options?: any) => void
      disableDraw: () => void
    }
  }
}
