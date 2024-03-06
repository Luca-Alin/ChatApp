import vuetify, { transformAssetUrls } from "vite-plugin-vuetify";

export default defineNuxtConfig({
    devtools: { enabled: true },
    build: {
        transpile: ["vuetify"]
    },
    modules: [
        '@pinia/nuxt',
        (_options, nuxt) => {
            nuxt.hooks.hook("vite:extendConfig", (config) => {
                config.plugins?.push(vuetify({ autoImport: true }));
            });
        }
    ],
    vite: {
        ssr: {
            noExternal: ['vuetify']
        },
        vue: {
            template: {
                transformAssetUrls
            }
        }
    },
    pinia: {
        storesDirs: ["./store/**"]
    }
});
