namespace ConsoleClient.Models
{
    public class ConversionResponseView
    {
        public string Category { get; set; } = "";
        public string FromUnit { get; set; } = "";
        public string ToUnit { get; set; } = "";
        public double InputValue { get; set; }
        public double ResultValue { get; set; }
    }
}