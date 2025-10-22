function togglePerfilMenu() {
    const menu = document.getElementById('perfilMenu');
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
  }
  
  function sair() {
    window.location.href = '/login';
  }
  
  // Fechar menu de perfil ao clicar fora
  window.addEventListener('click', (e) => {
    const menu = document.getElementById('perfilMenu');
    const user = document.querySelector('.user-info');
    const darkBtn = document.getElementById('darkToggle');
    if (!menu.contains(e.target) && !user.contains(e.target) && !darkBtn.contains(e.target)) {
      menu.style.display = 'none';
    }
  });
  