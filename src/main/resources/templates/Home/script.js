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
