using System.Runtime.Serialization;

namespace ConversionApp.Views
{
    [DataContract(Name = "conversionResponse", Namespace = "")]
    public class ConversionResponseView
    {
        [DataMember(Order = 1)]
        public string Category { get; set; }

        [DataMember(Order = 2)]
        public string FromUnit { get; set; }

        [DataMember(Order = 3)]
        public string ToUnit { get; set; }

        [DataMember(Order = 4)]
        public double InputValue { get; set; }

        [DataMember(Order = 5)]
        public double ResultValue { get; set; }

        public ConversionResponseView() { }

        public ConversionResponseView(string category, string fromUnit, string toUnit, double inputValue, double resultValue)
        {
            Category = category;
            FromUnit = fromUnit;
            ToUnit = toUnit;
            InputValue = inputValue;
            ResultValue = resultValue;
        }
    }
}
