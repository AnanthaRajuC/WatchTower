package com.hitherejoe.proximityapidemo;


import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;

import com.hitherejoe.proximityapidemo.android.R;
import com.hitherejoe.proximityapidemo.android.data.model.Attachment;
import com.hitherejoe.proximityapidemo.android.data.model.Beacon;
import com.hitherejoe.proximityapidemo.android.data.model.Namespace;
import com.hitherejoe.proximityapidemo.android.data.remote.ProximityApiService;
import com.hitherejoe.proximityapidemo.android.ui.activity.AttachmentsActivity;
import com.hitherejoe.proximityapidemo.android.util.MockModelsUtil;
import com.hitherejoe.proximityapidemo.util.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

public class AttachmentsActivityTest extends BaseTestCase<AttachmentsActivity> {

    public AttachmentsActivityTest() {
        super(AttachmentsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testAttachmentsDisplayed() throws Exception {
        Beacon beacon = MockModelsUtil.createMockRegisteredBeacon();
        List<Attachment> attachments = MockModelsUtil.createMockListOfAttachments(beacon.beaconName, 10);
        stubMockAttachments(beacon.beaconName, attachments);

        Intent i = new Intent(AttachmentsActivity.getStartIntent(getInstrumentation().getContext(), beacon));
        setActivityIntent(i);
        getActivity();

        checkAttachmentsDisplayOnRecyclerView(attachments);
    }

    public void testDeleteAttachment() throws Exception {
        Beacon beacon = MockModelsUtil.createMockRegisteredBeacon();
        List<Attachment> attachments = MockModelsUtil.createMockListOfAttachments(beacon.beaconName, 1);
        stubMockAttachments(beacon.beaconName, attachments);

        when(mProximityApiService.deleteAttachment(attachments.get(0).attachmentName))
                .thenReturn(Observable.<Void>empty());

        Intent i = new Intent(AttachmentsActivity.getStartIntent(getInstrumentation().getContext(), beacon));
        setActivityIntent(i);
        getActivity();

        onView(withId(R.id.text_delete))
                .perform(click());
        onView(withText(R.string.text_no_attachments))
                .check(matches(isDisplayed()));
    }

    public void testEmptyAttachmentsFeed() throws Exception {
        Beacon beacon = MockModelsUtil.createMockRegisteredBeacon();
        stubMockAttachments(beacon.beaconName, new ArrayList<Attachment>());
        Intent i = new Intent(AttachmentsActivity.getStartIntent(getInstrumentation().getContext(), beacon));
        setActivityIntent(i);
        getActivity();

        onView(withText(R.string.text_no_attachments))
                .check(matches(isDisplayed()));
    }

    public void testAddAttachmentActivityStarted() {
        Beacon beacon = MockModelsUtil.createMockRegisteredBeacon();
        stubMockAttachments(beacon.beaconName, new ArrayList<Attachment>());
        List<Namespace> namespaces = MockModelsUtil.createMockListOfNamespaces(1);
        ProximityApiService.NamespacesResponse namespacesResponse = new ProximityApiService.NamespacesResponse();
        namespacesResponse.namespaces = namespaces;

        when(mProximityApiService.getNamespaces())
                .thenReturn(Observable.just(namespacesResponse));

        Intent i = new Intent(AttachmentsActivity.getStartIntent(getInstrumentation().getContext(), beacon));
        setActivityIntent(i);
        getActivity();

        onView(withId(R.id.fab_add))
                .perform(click());
        onView(withId(R.id.spinner_namespace))
                .check(matches(isDisplayed()));
    }

    private void checkAttachmentsDisplayOnRecyclerView(List<Attachment> beaconsToCheck) {
        for (int i = 0; i < beaconsToCheck.size(); i++) {
            onView(withId(R.id.recycler_attachments))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            checkPostDisplays(beaconsToCheck.get(i));
        }
    }

    private void checkPostDisplays(Attachment attachment) {
        onView(withText(attachment.attachmentName))
                .check(matches(isDisplayed()));
    }

    private void stubMockAttachments(String beaconName, List<Attachment> mockAttachments) {
        ProximityApiService.AttachmentResponse attachmentResponse = new ProximityApiService.AttachmentResponse();
        attachmentResponse.attachments = mockAttachments;
        when(mProximityApiService.getAttachments(beaconName, null))
                .thenReturn(Observable.just(attachmentResponse));
    }
}