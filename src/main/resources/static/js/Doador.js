// ======== MODAL POSTAR PRODUTO ========
function abrirPostarProduto() {
  const modal = document.getElementById('postarModal');
  modal.style.display = 'block';
  setTimeout(() => modal.classList.add('show'), 10);
}

function fecharPostarProduto() {
  const modal = document.getElementById('postarModal');
  modal.classList.remove('show');
  setTimeout(() => modal.style.display = 'none', 300);
}

// ======== MODAL RANKING ========
function abrirRanking() {
  const modal = document.getElementById('rankingModal');
  modal.style.display = 'block';
  setTimeout(() => modal.classList.add('show'), 10);
}

function fecharRanking() {
  const modal = document.getElementById('rankingModal');
  modal.classList.remove('show');
  setTimeout(() => modal.style.display = 'none', 300);
}

// ======== FECHAR MODAL AO CLICAR FORA ========
window.addEventListener('click', (event) => {
  const modalPostar = document.getElementById('postarModal');
  const modalRanking = document.getElementById('rankingModal');
  if (event.target === modalPostar) fecharPostarProduto();
  if (event.target === modalRanking) fecharRanking();
});
