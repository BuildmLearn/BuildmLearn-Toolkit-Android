package org.buildmlearn.toolkit.flashcardtemplate.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.flashcardtemplate.Constants;
import org.buildmlearn.toolkit.flashcardtemplate.animations.FlipPageTransformer;
import org.buildmlearn.toolkit.flashcardtemplate.data.FlashDb;
import org.buildmlearn.toolkit.flashcardtemplate.data.FlashModel;
import org.buildmlearn.toolkit.flashcardtemplate.widgets.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by anupam on 10/8/16.
 */

/**
 * @brief Fragment containing the flash cards in dictation template's simulator.
 */
public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private FlashDb db;
    private View rootView;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_flash, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);
        toolbar.setTitle(getResources().getString(R.string.main_title_flash));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(String.format("%1$s", getString(R.string.comprehension_about_us)));
                        builder.setMessage(getResources().getText(R.string.comprehension_about_text));
                        builder.setPositiveButton("OK", null);
                        AlertDialog welcomeAlert = builder.create();
                        welcomeAlert.show();
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert ((TextView) welcomeAlert.findViewById(android.R.id.message)) != null;
                        ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                        break;
                    default: //do nothing
                        break;
                }
                return true;
            }
        });

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity()
                , drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        Context mContext = getActivity();

        db = new FlashDb(mContext);
        db.open();

        Bundle extras = getArguments();
        String FlashId = "1";
        if (extras != null) {
            FlashId = extras.getString(Intent.EXTRA_TEXT);
        }

        Menu m = navigationView.getMenu();
        SubMenu topChannelMenu = m.addSubMenu("Flash Cards");
        final long numFlash = db.getCountFlashCards();

        for (int i = 1; i <= numFlash; i++) {
            topChannelMenu.add(String.format(Locale.getDefault(), "Flash Card %1$d", i));
            topChannelMenu.getItem(i - 1).setIcon(R.drawable.ic_assignment_black_24dp);
            final int finalI = i;
            topChannelMenu.getItem(i - 1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(finalI));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    return false;
                }
            });
        }

        MenuItem mi = m.getItem(m.size() - 1);
        mi.setTitle(mi.getTitle());


        Cursor flash_cursor = db.getFlashCursorById(Integer.parseInt(FlashId));
        flash_cursor.moveToFirst();
        String question = flash_cursor.getString(Constants.COL_QUESTION);
        String answer = flash_cursor.getString(Constants.COL_ANSWER);
        String hint = flash_cursor.getString(Constants.COL_HINT);
        String base64 = flash_cursor.getString(Constants.COL_BASE64);

        assert rootView.findViewById(R.id.intro_number) != null;
        ((TextView) rootView.findViewById(R.id.intro_number)).setText(String.format(Locale.ENGLISH, "Card #%d of %d", Integer.parseInt(FlashId), numFlash));
        assert rootView.findViewById(R.id.question) != null;
        ((TextView) rootView.findViewById(R.id.question)).setText(question);
        assert rootView.findViewById(R.id.hint) != null;
        ((TextView) rootView.findViewById(R.id.hint)).setText(String.format(Locale.ENGLISH, "Hint : %s", hint));

        Button prv = (Button) rootView.findViewById(R.id.previous);
        Button flip = (Button) rootView.findViewById(R.id.flip);
        Button next = (Button) rootView.findViewById(R.id.next);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerflash);


        if (null != FlashId && FlashId.equals("1")) {
            assert prv != null;
            prv.setEnabled(false);
            prv.setTextColor(ContextCompat.getColor(mContext, R.color.grey_shade_4_flash));
        }

        viewPager.setPageTransformer(true, new FlipPageTransformer());

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateDecelerateInterpolator());
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalFlashId1 = FlashId;
        assert prv != null;
        prv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long nextFlashId = Integer.parseInt(finalFlashId1) - 1;

                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextFlashId));

                Fragment frag = MainFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

            }
        });

        final String finalFlashId = FlashId;
        assert next != null;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long nextFlashId = Integer.parseInt(finalFlashId) + 1;
                if (nextFlashId <= numFlash) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextFlashId));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();


                } else {

                    Bundle arguments = new Bundle();
                    Fragment frag = LastFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                }
            }
        });

        assert flip != null;
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0)
                    viewPager.setCurrentItem(1, true);
                else
                    viewPager.setCurrentItem(0, true);
            }
        });

        FlashModel mFlash = new FlashModel();
        mFlash.setQuestion(question);
        mFlash.setAnswer(answer);
        mFlash.setHint(hint);
        mFlash.setBase64(base64);
        setAdapterData(mFlash);

        return rootView;
    }

    private void setAdapterData(FlashModel mFlash) {
        MyPagerAdapter adapter = new MyPagerAdapter(mFlash);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MyPagerAdapter extends PagerAdapter {

        public final FlashModel model;

        public MyPagerAdapter(FlashModel mFlash) {
            this.model = mFlash;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View cardView;

            if (position == 0) {
                cardView = LayoutInflater.from(getContext()).inflate(R.layout.question_layout_flash_card, container, false);
                ImageView image = (ImageView) cardView.findViewById(R.id.questionimage);

                if (model.getBase64() != null && !model.getBase64().equals("")) {
                    byte[] decodedString = Base64.decode(model.getBase64(),
                            Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,
                            0, decodedString.length);
                    image.setImageBitmap(decodedByte);
                } else {
                    image.setImageResource(R.drawable.image_quiz);
                }
            } else {
                cardView = LayoutInflater.from(getContext()).inflate(R.layout.answer_layout_flash_card, container, false);
                TextView text = (TextView) cardView.findViewById(R.id.answertext);

                text.setText(model.getAnswer());
            }

            int width = getContext().getResources().getDisplayMetrics().widthPixels - getContext().getResources().getDimensionPixelSize(R.dimen.material_button_height_flash);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            cardView.findViewById(R.id.cardview).setLayoutParams(params);

            container.addView(cardView);
            return cardView;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
