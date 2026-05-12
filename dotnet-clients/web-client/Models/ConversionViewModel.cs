using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.Rendering;
using System.Collections.Generic;

namespace web_client.Models
{
    public class ConversionViewModel
    {
        [Required(ErrorMessage = "Debe seleccionar una categoria.")]
        [Display(Name = "Categoria")]
        public string Category { get; set; } = "Mass";

        [Required(ErrorMessage = "Debe ingresar un valor a convertir.")]
        [Range(0.0001, double.MaxValue, ErrorMessage = "El valor debe ser mayor a 0.")]
        [Display(Name = "Valor a Convertir")]
        public double? InputValue { get; set; }

        [Required(ErrorMessage = "Debe seleccionar la unidad de origen.")]
        [Display(Name = "Desde")]
        public string FromUnit { get; set; }

        [Required(ErrorMessage = "Debe seleccionar la unidad de destino.")]
        [Display(Name = "Hacia")]
        public string ToUnit { get; set; }

        public double? ResultValue { get; set; }
        public string? ErrorMessage { get; set; }
        public string? SuccessMessage { get; set; }

        public List<SelectListItem> Categories { get; } = new List<SelectListItem>
        {
            new SelectListItem { Value = "Mass", Text = "Masa" },
            new SelectListItem { Value = "Length", Text = "Longitud" },
            new SelectListItem { Value = "Temperature", Text = "Temperatura" }
        };

        public static Dictionary<string, string[]> UnitsByCategory { get; } = new Dictionary<string, string[]>
        {
            { "Mass", new[] { "KILOGRAM", "GRAM", "POUND", "OUNCE" } },
            { "Length", new[] { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" } },
            { "Temperature", new[] { "CELSIUS", "FAHRENHEIT", "KELVIN" } }
        };

        public string[] GetUnitsForCategory(string category)
        {
            return UnitsByCategory.TryGetValue(category, out var units) ? units : Array.Empty<string>();
        }
    }
}
