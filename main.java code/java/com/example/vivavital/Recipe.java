package com.example.vivavital;

public class Recipe {
    // Fields
    public String name;
    public String cookTime;
    public String calories;
    public int imageResId;       // Resource ID for the image (e.g., R.drawable.bakedegg)
    public String[] ingredients; // Array of ingredient strings
    public String instructions;  // Cooking steps

    // Constructor
    public Recipe(String name, String cookTime, String calories,
                  int imageResId, String[] ingredients, String instructions) {
        this.name = name;
        this.cookTime = cookTime;
        this.calories = calories;
        this.imageResId = imageResId;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }
}