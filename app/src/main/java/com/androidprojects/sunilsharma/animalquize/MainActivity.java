package com.androidprojects.sunilsharma.animalquize;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity
{
    //Todo : This is Step 1 For This Project
    //Todo : These are the "Key" value which m declared in quiz_preferences.xml file
    public static final String GUESSES = "settings_numberOfGuesses";
    public static final String ANIMAL_TYPE = "settings_animalsType";
    public static final String QUIZ_BACKGROUND_COLOR = "settings_quiz_background_color";
    public static final String QUIZ_FONT = "settings_quiz_font";

    //Todo : This is Step 2 For This Project
    //Todo : This Variable check whether setting are Changed or NOT
    private boolean isSettingsChanged = false;

    //Todo : This is Step 3 For This Project
    //Todo : These are Variable for Accessing Fonts Fonts which M using in this projects.
    static Typeface chinkfive;
    static Typeface fontlerybrown;
    static Typeface wonderbarDemo;

    MainActivityFragment myAnimalQuizFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Todo : This is Step 4 For This Project
        //Todo : Now Here M Initializing the Variable Which i'll Created for Fonts
        chinkfive = Typeface.createFromAsset(getAssets() , "fonts/Chinkfive.otf");
        fontlerybrown = Typeface.createFromAsset(getAssets() , "fonts/FontleroyBrown.ttf");
        wonderbarDemo = Typeface.createFromAsset(getAssets() , "fonts/FontleroyBrown.ttf");

        //Todo : This is Step 5 For This Project
        //Todo: Used to help create Preference hierarchies from activities or XML.
        // OR This Class Manage The Preferences in Our Classes.
        PreferenceManager.setDefaultValues(MainActivity.this , R.xml.quiz_preferences , false);



        //Todo : This is Step 6 For This Project
        //Todo : Here we want to get SharedPreference of our Application inside User Device
        //Because whenever user change the settings that store in this Method.
        //Todo : After that we have to create a Listener which notify every time whenever User changes in settings
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                registerOnSharedPreferenceChangeListener(settingChangedListener);

        myAnimalQuizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.animalQuizFragment);

        myAnimalQuizFragment.ModifyAnimalGuessRows(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifyTypeOfAnimalsInQuiz(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifyQuizFont(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.modifuBackgroundColor(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        myAnimalQuizFragment.resetAnimalQuiz();
        isSettingsChanged = false;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //Todo : This is Step 8 For This Project
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Todo : This is Step 9 For This Project

        /**
         * Now here m going to create a Intent for passing Data one Activity to Another Activity
         * which is 'SecondActivity'
         * */
        Intent preferenceIntent = new Intent(MainActivity.this , SettingsActivity.class);
        startActivity(preferenceIntent);

        return super.onOptionsItemSelected(item);
    }

    //Todo : This is Step 7 For This Project
    //Todo : Here We have to Create a Listener which we called in this method'registerOnSharedPreferenceChangeListener'

    /**
     * SharedPPreferences is a
     * Interface definition for a callback to be invoked when a shared
     * preference is changed.
     */

    /**
     * OnSharedPreferenceChangeListener()
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *            the change.
     * @param key The key of the preference that was changed, added, or
     *            removed.
     */

    private SharedPreferences.OnSharedPreferenceChangeListener settingChangedListener =
             new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
                {
                    isSettingsChanged = true;

                    if(key.equals(GUESSES))
                    {
                        myAnimalQuizFragment.ModifyAnimalGuessRows(sharedPreferences);
                        myAnimalQuizFragment.resetAnimalQuiz();
                    }
                    else if(key.equals(ANIMAL_TYPE))
                    {
                        Set<String> animalTypes = sharedPreferences.getStringSet(ANIMAL_TYPE , null);

                        if(animalTypes != null && animalTypes.size() > 0)
                        {
                            myAnimalQuizFragment.modifyTypeOfAnimalsInQuiz(sharedPreferences);
                            myAnimalQuizFragment.resetAnimalQuiz();
                        }
                        else
                        {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            animalTypes.add(getString(R.string.default_animal_type));
                            editor.putStringSet(ANIMAL_TYPE , animalTypes);
                            editor.apply();


                            Toast.makeText(MainActivity.this , R.string.toast_message , Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(key.equals(QUIZ_FONT))
                    {
                        myAnimalQuizFragment.modifyQuizFont(sharedPreferences);
                        myAnimalQuizFragment.resetAnimalQuiz();
                    }
                    else if(key.equals(QUIZ_BACKGROUND_COLOR))
                    {
                        myAnimalQuizFragment.modifuBackgroundColor(sharedPreferences);
                        myAnimalQuizFragment.resetAnimalQuiz();
                    }
                    Toast.makeText(MainActivity.this , R.string.toast_message , Toast.LENGTH_SHORT).show();
                }
            };
}
