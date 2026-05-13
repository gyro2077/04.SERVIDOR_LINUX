using System;

namespace ConsoleClient
{
    public static class ConsoleHelper
    {
        public static void ClearLine()
        {
            Console.SetCursorPosition(0, Console.CursorTop);
            Console.Write(new string(' ', Console.WindowWidth));
            Console.SetCursorPosition(0, Console.CursorTop);
        }

        public static int ShowSelectionMenu(string[] options, int startY = -1)
        {
            int currentSelection = 0;
            int topPosition = startY >= 0 ? startY : Console.CursorTop;
            ConsoleKey key;

            do
            {
                for (int i = 0; i < options.Length; i++)
                {
                    Console.SetCursorPosition(0, topPosition + i);
                    ClearLine();

                    if (i == currentSelection)
                    {
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.BackgroundColor = ConsoleColor.Cyan;
                        Console.Write($"  ► {options[i]}  ");
                        Console.ResetColor();
                    }
                    else
                    {
                        Console.Write($"    {options[i]}  ");
                    }
                }

                Console.SetCursorPosition(0, topPosition + options.Length);
                ClearLine();

                Console.WriteLine();
                Console.ResetColor();
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
                    Console.SetCursorPosition(0, topPosition);
                    for (int i = 0; i < options.Length + 1; i++)
                    {
                        ClearLine();
                        Console.WriteLine();
                    }
                    Console.SetCursorPosition(0, topPosition);
                    return currentSelection;
                }
            } while (key != ConsoleKey.Escape);

            Console.SetCursorPosition(0, topPosition);
            for (int i = 0; i < options.Length + 1; i++)
            {
                ClearLine();
                Console.WriteLine();
            }
            Console.SetCursorPosition(0, topPosition);
            return -1;
        }

        public static string ShowUnitSelection(string[] units)
        {
            Console.WriteLine("\nSeleccione unidad con ↑↓ y ENTER:");
            int selection = ShowSelectionMenu(units);
            if (selection >= 0)
            {
                return units[selection];
            }
            Console.Write("O escriba manualmente: ");
            string input = Console.ReadLine()?.Trim().ToUpper() ?? "";
            return input;
        }
    }
}