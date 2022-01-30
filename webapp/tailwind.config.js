module.exports = {
  content: ["../../../../src/main/html/index.html", "../../../../src/**/*.scala"],
  plugins: [
    require('daisyui'),
  ],
  daisyui: {
    logs: false, // otherwise daisy logs its ui version
    themes: [
      'light',
      'dark'
    ]
  },
};
