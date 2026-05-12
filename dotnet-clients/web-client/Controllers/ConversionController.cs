using Microsoft.AspNetCore.Mvc;
using web_client.Models;
using web_client.Services;
using web_client.Configuration;
using Microsoft.Extensions.Options;

namespace web_client.Controllers
{
    public class ConversionController : Controller
    {
        private readonly ISoapService _soapService;
        private readonly SoapServiceConfig _config;

        public ConversionController(ISoapService soapService, IOptions<SoapServiceConfig> config)
        {
            _soapService = soapService;
            _config = config.Value;
        }

        [HttpGet]
        public IActionResult Index()
        {
            var model = CreateDefaultModel();
            return View(model);
        }

        [HttpPost]
        // [ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(ConversionViewModel model)
        {
            if (!ModelState.IsValid)
            {
                return View(model);
            }

            var soapResponse = await _soapService.InvokeConversionAsync(
                model.Category,
                model.InputValue!.Value,
                model.FromUnit,
                model.ToUnit
            );

            if (soapResponse.Success)
            {
                model.ResultValue = soapResponse.ResultValue;
                model.SuccessMessage = $"Conversion exitosa: {model.InputValue} {model.FromUnit} = {soapResponse.ResultValue:F6} {model.ToUnit}";
                model.ErrorMessage = null;
            }
            else
            {
                model.ErrorMessage = soapResponse.ErrorMessage;
                model.SuccessMessage = null;
            }

            return View(model);
        }

        [HttpGet]
        public IActionResult Configure()
        {
            var model = new ServerConfigViewModel
            {
                ServerAddress = _config.ServerAddress,
                ServerPort = _config.ServerPort
            };
            return View(model);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Configure(ServerConfigViewModel model)
        {
            if (ModelState.IsValid)
            {
                _config.Configure(model.ServerAddress, model.ServerPort);
                TempData["SuccessMessage"] = "Configuracion guardada correctamente.";
                return RedirectToAction("Index");
            }

            return View(model);
        }

        private ConversionViewModel CreateDefaultModel(string category = "Mass")
        {
            var model = new ConversionViewModel
            {
                Category = category,
                FromUnit = ConversionViewModel.UnitsByCategory[category][0],
                ToUnit = ConversionViewModel.UnitsByCategory[category].Length > 1 
                    ? ConversionViewModel.UnitsByCategory[category][1] 
                    : ConversionViewModel.UnitsByCategory[category][0]
            };
            return model;
        }
    }

    public class ServerConfigViewModel
    {
        public string ServerAddress { get; set; } = "localhost";
        public string ServerPort { get; set; } = "8080";
    }
}