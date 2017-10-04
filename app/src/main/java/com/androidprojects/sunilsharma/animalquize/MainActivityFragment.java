package com.androidprojects.sunilsharma.animalquize;

import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static android.R.attr.breakStrategy;
import static android.R.attr.switchMinWidth;
import static android.R.attr.toolbarStyle;
import static android.R.attr.y;
import static com.androidprojects.sunilsharma.animalquize.MainActivity.GUESSES;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
{
    private static final int NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ = 10;

    private List<String> allAnimalsNameList;
    private List<String> animalsNamesQuizList;
    private Set<String> animalTypesInQuiz;
    private String correctAnimalsAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int numberOfAnimalGuessRows;
    private SecureRandom secureRandomNumber;
    private Handler handler;
    private Animation wrongAnswerAnimation;

    private LinearLayout animalQuizLinearLayout;
    private TextView textQuestionNumber;
    private ImageView imageAnimal;
    private LinearLayout[] rowsOfGuessButtonsInAnimalQuiz;
    private TextView textAnswer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        allAnimalsNameList = new ArrayList<>();
        animalsNamesQuizList = new ArrayList<>();
        secureRandomNumber = new SecureRandom();
        handler = new Handler();


        wrongAnswerAnimation = AnimationUtils.loadAnimation(getActivity() , R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(1);

        //Todo : initialize the variables which is present in fragment_main.xml

        animalQuizLinearLayout = (LinearLayout) view.findViewById(R.id.animalQuizLinearLayout);
        textQuestionNumber = (TextView) view.findViewById(R.id.textQuestionNumber);
        imageAnimal = (ImageView) view.findViewById(R.id.imageAnimal);

        rowsOfGuessButtonsInAnimalQuiz    =  new LinearLayout[3];
        rowsOfGuessButtonsInAnimalQuiz[0] = (LinearLayout) view.findViewById(R.id.firstRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[1] = (LinearLayout) view.findViewById(R.id.secondRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[2] = (LinearLayout) view.findViewById(R.id.thirdRowLinearLayout);

        textAnswer = (TextView) view.findViewById(R.id.textAnswer);

        for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
        {
            for(int column = 0 ; column < row.getChildCount() ; column++)
            {
                Button buttonGuess = (Button) row.getChildAt(column);
                buttonGuess.setOnClickListener(buttonGuessListener);
                buttonGuess.setTextSize(24);
            }
        }

        textQuestionNumber.setText(getString(R.string.question_text , 1 , NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));

      return view;
    }

    private View.OnClickListener buttonGuessListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view)
        {
            Button buttonGuess = ((Button) view);
            String guessesValue = buttonGuess.getText().toString();
            String answerValue = getTheExactAnimalName(correctAnimalsAnswer);
            ++numberOfAllGuesses;

            if(guessesValue.equals(answerValue))
            {
                ++numberOfRightAnswers;

                textAnswer.setText(answerValue + "!" + " RIGHT");

                //Todo : if user click the right button or right answer Then other button will be disable.
                //Then This Method will be Call
                disableQuizGuessButtons();

                //Todo : Now if the user completed all the quiz then this Statement will be call
                if(numberOfRightAnswers == NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ)
                {
                    DialogFragment animalQuizResults = new DialogFragment(){


                        @NonNull
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState)
                        {
                            //Todo : This show the Alert Dialog Box
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.results_string_value , numberOfAllGuesses ,
                                    (1000 / (double) numberOfAllGuesses)));

                            //Todo : This is a Button which is inside the Alert Dialog
                            //Todo : and set the onClickListener to the Button
                            builder.setPositiveButton(R.string.reset_animal_quiz, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    /**
                                     * This Method Reset The Quiz Again
                                    * */
                                    resetAnimalQuiz();
                                }
                            });

                            return builder.create();
                        }
                    };
                    //Todo : This code set Disable the AlertDialog Quit button.
                    animalQuizResults.setCancelable(false);
                    //Todo: we have to set 'show' method to our 'animalQuizResult' to shoe the Alert Dialog Box to the user
                    animalQuizResults.show(getFragmentManager() , "AnimalQuizResults");
                }
                //Todo : if Quiz NOT Finished then this 'else' will be Execute
                //Todo : and if user give the correct Answer of the quiz  it's wait for One second then the other quiz will be shown
                else
                {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            animateAnimalQuiz(true);
                        }
                    },1000);//1000 milliseconds for 1 second.
                }

            }
            //Todo : if the User Gives Wrong Answer the this Block Of Code will be Execute
            else
            {
                imageAnimal.startAnimation(wrongAnswerAnimation);

                textAnswer.setText(R.string.wrong_answer_message);
                buttonGuess.setEnabled(false);
            }
        }
    };
    
    //// TODO: 10/3/2017  This code of block check the WHether user click the Right animal Name or NOT
    @NonNull
    private String getTheExactAnimalName(String animalName)
    {
        return animalName.substring(animalName.indexOf('-') + 1).replace('_' , ' ');
    }

    //Todo : if user click the right button or right answer then other button will be disable.
    //Todo : And If the User CLick on Wrong Button Then Also That Button is Disable
    //Then This Method will be Call

    private void disableQuizGuessButtons()
    {
        for(int row = 0 ; row < numberOfAnimalGuessRows ; row ++)
        {
            LinearLayout guessRowLinearLayout = rowsOfGuessButtonsInAnimalQuiz[row];

            for(int buttonIndex = 0 ; buttonIndex < guessRowLinearLayout.getChildCount() ; buttonIndex++)
            {
                guessRowLinearLayout.getChildAt(buttonIndex).setEnabled(false);
            }
        }
    }

    //Todo : This Method Call When user Click on 'reset_animal_quiz' this String present in string.xml file
    //Todo : This message shown in Alert Dialog Box
    //Todo : And When ever User Change the Settings of the Quiz Then Also This Method is Call.
    public void resetAnimalQuiz()
    {
        /**
         * Provides access to an application's raw asset files
         * for the way most applications will want to retrieve their resource data.
         * This class presents a lower-level API that allows you to open and read raw
         * files that have been bundled with the application as a simple stream of
         * bytes.
         */
        AssetManager assets = getActivity().getAssets();

        /**
         * When Ever user Change the Setting of Quiz then this code clear all the Animal List and
         * Reset the Animal List Again
         * */
        allAnimalsNameList.clear();

        /**
         * In This Try block we Manage Which Type of Animal Display in Quiz
         * Like If the User select the Wild Type Animal Then Only Wild Type animal Quiz Or Image will be display
         * */
        try
        {
            for (String animalType : animalTypesInQuiz)
            {
                String[] animalImagePathsInQuiz = assets.list(animalType);
                for(String animalImagePathInQuiz : animalImagePathsInQuiz)
                {
                    allAnimalsNameList.add(animalImagePathInQuiz.replace(".png" , ""));
                }
            }
        }
        catch(IOException e)
        {
            Log.e("AnimalQuiz" , "Error" , e);
        }

        numberOfRightAnswers = 0;
        numberOfAllGuesses = 0;
        animalsNamesQuizList.clear();

        int counter = 1;

        int numberOfAvailableAnimals = allAnimalsNameList.size();

        while (counter <= NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ)
        {
            int randomIndex = secureRandomNumber.nextInt(numberOfAvailableAnimals);
            String animalImageName = allAnimalsNameList.get(randomIndex);

            if(!animalsNamesQuizList.contains(animalImageName))
            {
                animalsNamesQuizList.add(animalImageName);
                ++counter;
            }
        }
        showNextAnimal();
    }

    private void animateAnimalQuiz(boolean animateOutAnimalImage)
    {
        if(numberOfRightAnswers == 0)
        {
            return;
        }
        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();


        /**
         * Because m performing Circular Animation
         * Hence i have to find out the Radius
         * Here is MAX value for Radius
         * */
        int radius = Math.max(animalQuizLinearLayout.getWidth() , animalQuizLinearLayout.getHeight());

        Animator animator;

        //If we Pass true to this then this statement execute
        if(animateOutAnimalImage)
        {
            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout ,
                    xBottomRight , yBottomRight , radius , 0);

            /**
             *Now we have to Add Listener Here
            */
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    showNextAnimal();
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });
        }
        else
        {
            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout ,
                            xTopLeft , yTopLeft , 0 , radius);
        }
        animator.setDuration(700);
        animator.start();
    }

    private void showNextAnimal()
    {
        String nextAnimalImageName = animalsNamesQuizList.remove(0);
        correctAnimalsAnswer = nextAnimalImageName;
        textAnswer.setText("");

        textQuestionNumber.setText(getString(R.string.question_text ,
                (numberOfRightAnswers + 1) , NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));

        /**
         * This Purpose for this statement is to get the Animal Type
         * Mean whether the animal Type is 'Wild' OR 'Tame'
         * */
        String animalType = nextAnimalImageName.substring(0 , nextAnimalImageName.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        try(InputStream stream = assets.open(animalType + "/" + nextAnimalImageName + ".png"))
        {
            Drawable animalImage = Drawable.createFromStream(stream , nextAnimalImageName);

            imageAnimal.setImageDrawable(animalImage);

            animateAnimalQuiz(false);
        }
        catch(IOException e)
        {
            Log.e("AnimalQuiz" , "There is an Error Getting" + nextAnimalImageName , e);
        }

        Collections.shuffle(allAnimalsNameList);

        int correctAnimalNameIndex = allAnimalsNameList.indexOf(correctAnimalsAnswer);
        String correctAnimalName = allAnimalsNameList.remove(correctAnimalNameIndex);
        allAnimalsNameList.add(correctAnimalName);


        for(int row =0 ; row < numberOfAnimalGuessRows ; row++)
        {
            for(int column = 0 ; column < rowsOfGuessButtonsInAnimalQuiz[row].getChildCount() ; column++)
            {
                Button buttonGuess = (Button) rowsOfGuessButtonsInAnimalQuiz[row].getChildAt(column);
                buttonGuess.setEnabled(true);

                String animalImageName = allAnimalsNameList.get((row * 2) + column);
                buttonGuess.setText(getTheExactAnimalName(animalImageName));
            }
        }

        int row = secureRandomNumber.nextInt(numberOfAnimalGuessRows);
        int column = secureRandomNumber.nextInt(2);
        LinearLayout randomRow = rowsOfGuessButtonsInAnimalQuiz[row];
        String correctAnimalImageName = getTheExactAnimalName(correctAnimalsAnswer);
        ((Button) randomRow.getChildAt(column)).setText(correctAnimalImageName);
    }


    public void ModifyAnimalGuessRows(SharedPreferences sharedPreferences)
    {
        final String NUMBER_OF_GUESS_OPTIONS = sharedPreferences.getString(MainActivity.GUESSES , null);
        numberOfAnimalGuessRows = Integer.parseInt(NUMBER_OF_GUESS_OPTIONS) / 2;

        for(LinearLayout horizontalLinearLayout : rowsOfGuessButtonsInAnimalQuiz)
        {
            horizontalLinearLayout.setVisibility(View.GONE);
        }

        for(int row = 0 ; row < numberOfAnimalGuessRows ; row++)
        {
            rowsOfGuessButtonsInAnimalQuiz[row].setVisibility(View.VISIBLE);
        }
    }

    public void modifyTypeOfAnimalsInQuiz(SharedPreferences sharedPreferences)
    {
        animalTypesInQuiz = sharedPreferences.getStringSet(MainActivity.ANIMAL_TYPE , null);
    }

    public void modifyQuizFont(SharedPreferences sharedPreferences)
    {
        String fontsStringValue = sharedPreferences.getString(MainActivity.QUIZ_FONT , null);

        switch (fontsStringValue)
        {
            case "Chunkfive.otf":
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.chinkfive);
                    }
                }
                break;

            case "FontleroyBrown.ttf":
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.fontlerybrown);
                    }
                }
                break;

            case "Wonderbar Demo.otf":
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.wonderbarDemo);
                    }
                }
                break;
        }
    }

    public void modifuBackgroundColor(SharedPreferences sharedPreferences)
    {
        String backgroundColor = sharedPreferences.getString(MainActivity.QUIZ_BACKGROUND_COLOR , null);

        switch (backgroundColor)
        {
            case "White":
                animalQuizLinearLayout.setBackgroundColor(Color.WHITE);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }
                textAnswer.setTextColor(Color.BLUE);
                textQuestionNumber.setTextColor(Color.BLACK);

                break;

            case "Black":
                animalQuizLinearLayout.setBackgroundColor(Color.BLACK);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);
                    }
                }
                textAnswer.setTextColor(Color.WHITE);
                textQuestionNumber.setTextColor(Color.WHITE);
                break;

            case "Green":
                animalQuizLinearLayout.setBackgroundColor(Color.GREEN);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }
                textAnswer.setTextColor(Color.WHITE);
                textQuestionNumber.setTextColor(Color.YELLOW);
                break;

            case "Blue":
                animalQuizLinearLayout.setBackgroundColor(Color.BLUE);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);
                    }
                }
                textAnswer.setTextColor(Color.WHITE);
                textQuestionNumber.setTextColor(Color.WHITE);
                break;

            case "Red":
                animalQuizLinearLayout.setBackgroundColor(Color.RED);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);
                    }
                }
                textAnswer.setTextColor(Color.WHITE);
                textQuestionNumber.setTextColor(Color.WHITE);
                break;

            case "Yellow":
                animalQuizLinearLayout.setBackgroundColor(Color.YELLOW);
                for(LinearLayout row : rowsOfGuessButtonsInAnimalQuiz)
                {
                    for (int column = 0 ; column<row.getChildCount() ; column++)
                    {
                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLACK);
                        button.setTextColor(Color.WHITE);
                    }
                }
                textAnswer.setTextColor(Color.BLACK);
                textQuestionNumber.setTextColor(Color.BLACK);
                break;


        }
    }

}
