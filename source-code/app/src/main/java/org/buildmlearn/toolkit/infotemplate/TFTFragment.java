/* Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 
 * Neither the name of the BuildmLearn nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.buildmlearn.toolkit.infotemplate;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;

public class TFTFragment extends ListFragment {


    public static Fragment newInstance(String path) {
        TFTFragment fragment = new TFTFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SIMULATOR_FILE_PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        GlobalData gd = GlobalData.getInstance();
        gd.readXml(getArguments().getString(Constants.SIMULATOR_FILE_PATH));
        InfoListAdapter adapter = new InfoListAdapter(getActivity());
        setListAdapter(adapter);
        setEmptyText("No Data");
        getListView().setBackgroundColor(getResources().getColor(R.color.info_template_blue_background));
        adapter.setList(gd.mList);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, DetailView.newInstance(position)).addToBackStack(null).commit();
    }


}