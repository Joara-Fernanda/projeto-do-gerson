/* ======== DARK MODE GLOBAL ======== */
document.addEventListener("DOMContentLoaded", () => {
    const toggleBtn = document.getElementById("darkToggle");
    if (!toggleBtn) return; // Se não tiver o botão na página, sai
  
    // Alterna o tema
    toggleBtn.addEventListener("click", () => {
      document.body.classList.toggle("dark");
      const icon = toggleBtn.querySelector("i");
      if(document.body.classList.contains("dark")) {
        icon.classList.replace("fa-moon","fa-sun");
        localStorage.setItem("theme","dark");
      } else {
        icon.classList.replace("fa-sun","fa-moon");
        localStorage.setItem("theme","light");
      }
    });
  
    // Aplica tema salvo ao carregar
    const saved = localStorage.getItem("theme");
    if(saved === "dark") {
      document.body.classList.add("dark");
      const icon = toggleBtn.querySelector("i");
      if(icon) icon.classList.replace("fa-moon","fa-sun");
    }
  });
  