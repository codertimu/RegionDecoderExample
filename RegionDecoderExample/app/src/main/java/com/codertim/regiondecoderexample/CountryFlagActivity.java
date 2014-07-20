package com.codertim.regiondecoderexample;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class CountryFlagActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_flag);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CountryFlagFragment())
                    .commit();
        }
    }

    public static class CountryFlagFragment extends Fragment {

        private static final String[] COUNTRY_NAMES =
                {
                        "Belgium", "Bosnia", "Germany", "Russia",
                        "Ecuador", "France", "Columbia", "Costa Rica",
                        "South Korea", "Netherlands", "Honduras", "Ghana",
                        "Cameroon", "Cote d'Ivoire", "Croatia", "USA",
                        "Mexico", "Nigeria", "Portugal", "Japan",
                        "Switzerland", "Uruguay", "Spain", "Greece",
                        "Iran", "Italy", "England", "Chile",
                        "Algeria", "Argentina", "Australia", "Brazil"
                };
        private static final int NUM_OF_FLAGS = 32;
        private static int COUNT_PER_ROW = 8;
        private static int SPACE = 70;

        private BitmapRegionDecoder mDecoder;
        private ImageView imgCountry;
        private TextView txtCountry;
        private SeekBar seekBarCountry;

        public CountryFlagFragment() {

        }

        /**
         * Creates BitmapRegionDecoder from an image in assets.
         */
        private void createDecoder(){
            InputStream is = null;
            try {
                is = getActivity().getAssets().open("brazil_wc_teams.png");
                mDecoder = BitmapRegionDecoder.newInstance(new BufferedInputStream(is), true);
            } catch (IOException e) {
                throw new RuntimeException("Could not create BitmapRegionDecoder", e);
            }
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            createDecoder();
            // show first country at start
            showCountry(0);
            txtCountry.setText(COUNTRY_NAMES[0]);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_country_flag, container, false);
            txtCountry = (TextView) rootView.findViewById(R.id.txtState);
            imgCountry = (ImageView) rootView.findViewById(R.id.imageView);
            seekBarCountry = (SeekBar) rootView.findViewById(R.id.verticalSeekBar);

            seekBarCountry.setMax(NUM_OF_FLAGS - 1);
            seekBarCountry.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    txtCountry.setText(COUNTRY_NAMES[i]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    imgCountry.setImageResource(R.drawable.question_mark);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int i = seekBar.getProgress();
                    showCountry(i);
                }
            });
            seekBarCountry.setProgress(0);
            return rootView;
        }

        private void showCountry(int i) {
            Bitmap bitmap = getFlag(i);
            imgCountry.setImageBitmap(bitmap);
        }

        /**
         * Decodes the region of bitmap.
         * @param i The index of the flag.
         * @return The bitmap of the specified country flag.
         */
        private Bitmap getFlag(int i) {
            Bitmap bitmap = null;
            bitmap = mDecoder.decodeRegion(getRectForIndex(i, mDecoder.getWidth()), null);
            return bitmap;
        }

        /**
         * @param index The index of the flag of one of the 32 countries.
         * @param width The width of the source image.
         * @return
         */
        private Rect getRectForIndex(int index, int width) {
            // check if index is valid
            if (index < 0 && index >= NUM_OF_FLAGS)
                throw new IllegalArgumentException("Index must be between 0 and 31.");

            // calculate one side of a single flag
            int oneSide = (width - ((COUNT_PER_ROW + 1) * SPACE)) / COUNT_PER_ROW;

            // calculate the row and col of the given index
            int row = (index / COUNT_PER_ROW);
            int col = (index % COUNT_PER_ROW);

            // left and right sides of the rectangle
            int left = (oneSide * col) + (SPACE * (col + 1));
            int right = left + oneSide;

            // top and bottom sides of the rectangle
            int top = (oneSide * row) + (SPACE * (row + 1));
            int bottom = top + oneSide;

            // the resulting rectangle
            return new Rect(left, top, right, bottom);
        }
    }
}
