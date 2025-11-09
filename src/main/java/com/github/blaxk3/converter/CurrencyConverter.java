package com.github.blaxk3.converter;

import com.github.blaxk3.ui.UI;

import javax.swing.SwingUtilities;

/**
 * Kelas yang mengurusi panggilan UI
 */

public class CurrencyConverter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UI::new);
    }
}
