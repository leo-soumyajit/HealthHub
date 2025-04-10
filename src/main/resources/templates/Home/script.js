function getStarted() {
    // Replace with your actual route
    window.location.href = "/login";  // or "/dashboard", or any service page
}
// FAQ toggle behavior
document.querySelectorAll('.faq-item').forEach(item => {
  item.addEventListener('click', () => {
    item.classList.toggle('open');
  });
});

setInterval(() => {
  window.location.reload();
}, 60000); // 60000ms = 1 minute
