package com.lighthouse.android.chats.uikit.channel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.lighthouse.android.chats.viewmodel.ChatViewModel
import com.lighthouse.android.common_ui.util.Injector
import com.lighthouse.android.common_ui.util.StringSet
import com.lighthouse.navigation.MainNavigator
import com.sendbird.android.channel.GroupChannel
import com.sendbird.android.params.UserMessageCreateParams
import com.sendbird.uikit.fragments.ChannelFragment
import com.sendbird.uikit.model.ReadyStatus
import com.sendbird.uikit.modules.ChannelModule
import com.sendbird.uikit.modules.components.MessageInputComponent
import com.sendbird.uikit.vm.ChannelViewModel
import dagger.hilt.android.EntryPointAccessors

open class CustomChannel : ChannelFragment() {
    private val viewModels: ChatViewModel by activityViewModels()
    private val mainNavigator: MainNavigator by lazy {
        EntryPointAccessors.fromActivity(
            requireActivity(),
            Injector.MainNavigatorInjector::class.java
        ).mainNavigator()
    }

    override fun onCreateModule(args: Bundle): ChannelModule {
        var module: ChannelModule = super.onCreateModule(args)

        module.setHeaderComponent(CustomChannelHeader())
        module.setInputComponent(CustomMessageInputComponent())
        com.sendbird.uikit.R.id.sb_fragment_container
        return module
    }


    override fun onBindMessageInputComponent(
        inputComponent: MessageInputComponent,
        viewModel: ChannelViewModel,
        channel: GroupChannel?,
    ) {
        super.onBindMessageInputComponent(inputComponent, viewModel, channel)

        if (inputComponent is CustomMessageInputComponent) {
            val customInput = module.messageInputComponent as CustomMessageInputComponent
            customInput.cameraInput = View.OnClickListener {
                takePhoto()
            }
            customInput.voiceInput = View.OnClickListener {
                takeVoiceRecorder()
            }

            viewModels.question.observe(this) {
                val params = UserMessageCreateParams(it).apply {
                    customType = StringSet.question_type
                }
                channel?.sendUserMessage(params) { m, e ->
                    if (e != null) {
                        //error handling
                    }

                }
            }
        }
    }

    override fun onBeforeReady(
        status: ReadyStatus,
        module: ChannelModule,
        viewModel: ChannelViewModel,
    ) {
        super.onBeforeReady(status, module, viewModel)
        val channel = viewModel.channel ?: return

        module.messageListComponent.setAdapter(CustomListAdapter(channel, true) { id, isMe ->
            mainNavigator.navigateToProfile(
                requireContext(),
                Pair("userId", id),
                Pair("isMe", isMe)
            )
        })
    }

}