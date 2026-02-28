import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
  server:{
    host: true
  },
  preview:{
    allowHosts: true,
    host: ['todolist.app.com']
  }
  // plugins: [react()],
  // preview: {
  //   host: true,
  //   port: 4173
  // }
})
