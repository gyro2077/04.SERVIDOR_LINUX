namespace desktop_client
{
    public static class AppConfig
    {
        public static string ServerAddress { get; set; } = "localhost";

        public static string ServerPort { get; set; } = "8080";

        public static string EndpointAddress => $"http://{ServerAddress}:{ServerPort}/ConversionService.svc";

        public static void Configure(string serverAddress, string serverPort = "8080")
        {
            ServerAddress = serverAddress;
            ServerPort = serverPort;
        }
    }
}
