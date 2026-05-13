using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using web_client.Models;
using web_client.Services;

namespace web_client.Controllers
{
    [Authorize]
    public class ConversionController : Controller
    {
        private readonly ISoapService _soapService;

        public ConversionController(ISoapService soapService)
        {
            _soapService = soapService;
        }

        [HttpGet]
        public IActionResult Index()
        {
            return View(CreateDefaultModel());
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(ConversionViewModel model)
        {
            if (!ModelState.IsValid)
                return View(model);

            var soapResponse = await _soapService.InvokeConversionAsync(
                model.Category,
                model.InputValue!.Value,
                model.FromUnit,
                model.ToUnit
            );

            if (soapResponse.Success)
            {
                model.ResultValue = soapResponse.ResultValue;
                model.SuccessMessage = $"Conversión exitosa: {model.InputValue} {model.FromUnit} = {soapResponse.ResultValue:F6} {model.ToUnit}";
                model.ErrorMessage = null;
            }
            else
            {
                model.ErrorMessage = soapResponse.ErrorMessage;
                model.SuccessMessage = null;
            }

            return View(model);
        }

        private static ConversionViewModel CreateDefaultModel(string category = "Mass")
        {
            return new ConversionViewModel
            {
                Category = category,
                FromUnit = ConversionViewModel.UnitsByCategory[category][0],
                ToUnit = ConversionViewModel.UnitsByCategory[category].Length > 1
                    ? ConversionViewModel.UnitsByCategory[category][1]
                    : ConversionViewModel.UnitsByCategory[category][0]
            };
        }
    }
}