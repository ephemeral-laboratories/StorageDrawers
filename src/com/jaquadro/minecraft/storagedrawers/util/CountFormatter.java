package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CountFormatter
{
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private static final NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    static
    {
        numberFormat.setMaxDecimalDigits(1);
    }

    public static String format (FontRenderer font, IDrawer drawer) {
        return formatApprox(font, drawer);
    }

    public static String formatStackNotation (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        String text;
        int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
        int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());

        if (stacks > 0 && remainder > 0)
            text = integerFormat.format(stacks) + "x" + integerFormat.format(drawer.getStoredItemStackSize()) +
                    "+" + integerFormat.format(remainder);
        else if (stacks > 0)
            text = integerFormat.format(stacks) + "x" + integerFormat.format(drawer.getStoredItemStackSize());
        else
            text = integerFormat.format(remainder);

        return text;
    }

    public static String formatExact (IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        return integerFormat.format(drawer.getStoredItemCount());
    }

    public static String formatApprox (FontRenderer font, IDrawer drawer) {
        if (drawer == null || drawer.isEmpty())
            return "";

        int count = drawer.getStoredItemCount();
        return formatQuantity(count);
    }

    public static String formatQuantity(FontRenderer font, String text, int stackSize)
    {
        if (qty >= (12*12*12*12))
        {
            int exp = (int) (Math.log(qty) / Math.log(12));
            float qtyShort = (float) (qty / Math.pow(12, exp));
            return numberFormat.format(qtyShort) + " " + exponentToAbbreviation(exp);
        }

        return integerFormat.format(qty);
    }

    private static String exponentToAbbreviation(int exponent)
    {
        String numeric = formatterWithoutGrouping.format(exponent);
        char[] chars = numeric.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            switch (chars[i])
            {
                case '0': chars[i] = 'n'; break;
                case '1': chars[i] = 'u'; break;
                case '2': chars[i] = 'b'; break;
                case '3': chars[i] = 't'; break;
                case '4': chars[i] = 'q'; break;
                case '5': chars[i] = 'p'; break;
                case '6': chars[i] = 'h'; break;
                case '7': chars[i] = 's'; break;
                case '8': chars[i] = 'o'; break;
                case '9': chars[i] = 'e'; break;
                case '\u218A': chars[i] = 'd'; break;
                case '\u218B': chars[i] = 'l'; break;
            }
        }
        return String.valueOf(chars);
    }
}
