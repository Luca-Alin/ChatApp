<script setup lang="ts">
</script>

<template>
  <div class="d-flex flex-column align-center justify-center">
    <router-link to="/">
      <div>
        <div v-if="$vuetify.display.xl"><h1>Home (XL)</h1></div>
        <div v-else-if="$vuetify.display.lg"><h1>Home (LG)</h1></div>
        <div v-else-if="$vuetify.display.md"><h1>Home (MD)</h1></div>
        <div v-else-if="$vuetify.display.sm"><h1>Home (SM)</h1></div>
        <div v-else><h1>Home (XS)</h1></div>
      </div>
    </router-link>
  </div>
</template>

<style scoped>
a {
  color: white;
}
</style>