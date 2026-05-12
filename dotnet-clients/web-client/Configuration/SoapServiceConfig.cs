namespace web_client.Configuration
{
    public class SoapServiceConfig
    {
        public string ServerAddress { get; set; } = "localhost";
        public string ServerPort { get; set; } = "8080";
        public string EndpointAddress => $"http://{ServerAddress}:{ServerPort}/ConversionService.svc";
        public string SoapNamespace => "http://ws.grupo3.edu.ec/";

        public void Configure(string serverAddress, string serverPort = "8080")
        {
            ServerAddress = serverAddress;
            ServerPort = serverPort;
        }
    }

    public static class ServiceExtensions
    {
        public static IServiceCollection AddSoapServiceConfiguration(this IServiceCollection services, IConfiguration configuration)
        {
            services.Configure<SoapServiceConfig>(options =>
            {
                options.ServerAddress = configuration["SoapService:ServerAddress"] ?? "localhost";
                options.ServerPort = configuration["SoapService:ServerPort"] ?? "8080";
            });

            return services;
        }
    }
}