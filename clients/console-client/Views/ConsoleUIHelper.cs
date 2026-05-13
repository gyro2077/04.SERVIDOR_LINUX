using System;

namespace ConsoleClient.Views
{
    public static class ConsoleUIHelper
    {
        public static void PrintHeader(string title)
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔════════════════════════════════════════════════════════════╗");
            Console.WriteLine($"║ {AlignCenter(title, 58)} ║");
            Console.WriteLine("╚════════════════════════════════════════════════════════════╝");
            Console.ResetColor();
            Console.WriteLine();
        }

        public static void PrintError(string message)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"❌ [Error]: {message}");
            Console.ResetColor();
        }

        public static void PrintSuccess(string message)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine($"✅ {message}");
            Console.ResetColor();
        }

        public static string ReadMaskedPassword()
        {
            string password = "";
            ConsoleKeyInfo info;

            while (true)
            {
                info = Console.ReadKey(true);
                if (info.Key == ConsoleKey.Enter)
                {
                    Console.WriteLine();
                    break;
                }
                else if (info.Key == ConsoleKey.Backspace)
                {
                    if (password.Length > 0)
                    {
                        password = password.Substring(0, password.Length - 1);
                        Console.Write("\b \b");
                    }
                }
                else if (!char.IsControl(info.KeyChar))
                {
                    password += info.KeyChar;
                    Console.Write("*");
                }
            }
            return password;
        }

        public static int ShowInteractiveMenu(string[] options)
        {
            int currentSelection = 0;
            int startTop = Console.CursorTop;
            ConsoleKey key;

            do
            {
                for (int i = 0; i < options.Length; i++)
                {
                    Console.SetCursorPosition(0, startTop + i);
                    Console.Write(new string(' ', Console.WindowWidth));
                    Console.SetCursorPosition(0, startTop + i);

                    if (i == currentSelection)
                    {
                        Console.BackgroundColor = ConsoleColor.Cyan;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.Write($"  ► {options[i]}  ");
                        Console.ResetColor();
                    }
                    else
                    {
                        Console.Write($"    {options[i]}  ");
                    }
                }

                key = Console.ReadKey(true).Key;

                if (key == ConsoleKey.UpArrow)
                {
                    currentSelection = currentSelection > 0 ? currentSelection - 1 : options.Length - 1;
                }
                else if (key == ConsoleKey.DownArrow)
                {
                    currentSelection = currentSelection < options.Length - 1 ? currentSelection + 1 : 0;
                }
                else if (key == ConsoleKey.Enter)
                {
                    Console.SetCursorPosition(0, startTop + options.Length);
                    Console.WriteLine();
                    return currentSelection;
                }

            } while (key != ConsoleKey.Escape);

            return -1;
        }

        private static string AlignCenter(string text, int width)
        {
            if (text.Length >= width) return text.Substring(0, width);
            int leftPadding = (width - text.Length) / 2;
            return text.PadLeft(leftPadding + text.Length).PadRight(width);
        }
    }
}