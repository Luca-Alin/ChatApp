<script setup lang="ts">

onMounted(() => {
  const main = document.getElementById("main")!;

  setInterval(() => {
    const newHeight = document.getElementById("content")!.offsetHeight;

    const header = document.getElementById("header")!.offsetHeight;
    const footer = document.getElementById("footer")!.offsetHeight;
    const contentMaxHeight = newHeight - (header + footer);

    main.style.height = `${contentMaxHeight}px`;
    main.style.maxHeight = `${contentMaxHeight}px`;
    main.style.minHeight = `${contentMaxHeight}px`;
  }, 100 / 60);

});
</script>


<template>

  <div id="content" style="height: 100vh; max-height: 100vh">

    <div id="header">
      <AppHeader class="bg-blue-darken-4"/>
    </div>

    <div id="main" style="display: flex;">
      <div id="div2" style="flex: 1 1 auto; height: 100%; width: 100%">
        <div style="height: 100%; width: 100%;">
          <NuxtPage/>
        </div>
      </div>
    </div>

    <div id="footer">
      <AppFooter class="bg-blue-darken-4"/>
    </div>
  </div>


</template>

<style scoped>

</style>

